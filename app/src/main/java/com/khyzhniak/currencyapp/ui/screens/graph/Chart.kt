package com.khyzhniak.currencyapp.ui.screens.graph

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.khyzhniak.currencyapp.domain.model.DateRangePreset
import com.khyzhniak.currencyapp.domain.model.HistoryRates
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayerDimensions
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RateSummaryHeader(
    fromCurrency: String,
    toCurrency: String,
    latestRate: Double,
    percentChange: Double,
    absoluteChange: Double,
    modifier: Modifier = Modifier
) {
    val isPositive = percentChange >= 0

    val changeColor = if (isPositive) {
        Color(0xFF4CAF50)
    } else {
        Color(0xFFF44336)
    }

    val arrow = if (isPositive) "↑" else "↓"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "1 $fromCurrency =",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = String.format(Locale.US, "%.4f", latestRate),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = toCurrency,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Text(
            text = "$arrow ${String.format(Locale.US, "%.2f", percentChange)}% " +
                    "(${if (isPositive) "+" else ""}" +
                    String.format(Locale.US, "%.4f", absoluteChange) + ")",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = changeColor
        )
    }
}

private const val LABEL_COUNT = 5
private const val LABEL_SEGMENTS = LABEL_COUNT - 1
private const val MAX_POINTS_BETWEEN_LABELS = 12
private data class ChartSample(
    val points: List<HistoryRates>,
    val labelSpacing: Int,
)

private val shortDateFormatter = DateTimeFormatter.ofPattern("dd.MM", Locale.US)
private val compactMonthYearFormatter = DateTimeFormatter.ofPattern("MM.yy", Locale.US)

private fun formatAxisDate(dateString: String, preset: DateRangePreset): String =
    try {
        val formatter = when (preset) {
            DateRangePreset.ONE_YEAR, DateRangePreset.FIVE_YEARS -> compactMonthYearFormatter
            else -> shortDateFormatter
        }
        LocalDate.parse(dateString).format(formatter)
    } catch (_: DateTimeParseException) {
        dateString
    }

private fun sampledForChart(historyRates: List<HistoryRates>): ChartSample {
    val sorted = historyRates.sortedBy { it.date }
    val intervals = sorted.size - 1

    if (intervals < 2 * LABEL_SEGMENTS) return ChartSample(sorted, labelSpacing = 1)

    val spacesPerLabel = minOf(intervals / LABEL_SEGMENTS, MAX_POINTS_BETWEEN_LABELS)
    val lastIndex = spacesPerLabel * LABEL_SEGMENTS
    val stride = intervals.toDouble() / lastIndex

    val points = List(lastIndex + 1) { i ->
        sorted[(i * stride).roundToInt()]
    }

    return ChartSample(points = points, labelSpacing = spacesPerLabel)
}

private val CHART_HEIGHT = 240.dp
private val Y_AXIS_WIDTH = 52.dp
private val EDGE_PADDING = 20.dp

@Composable
fun CurrencyVicoChart(
    historyRates: List<HistoryRates>,
    selectedPreset: DateRangePreset,
    modifier: Modifier = Modifier
) {
    if (historyRates.isEmpty()) return

    val sample = remember(historyRates) { sampledForChart(historyRates) }
    val points = sample.points

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(points) {
        modelProducer.runTransaction {
            lineSeries { series(points.map { it.rate }) }
        }
    }

    val minY = points.minOf { it.rate } * 0.995
    val maxY = points.maxOf { it.rate } * 1.005

    val rangeProvider = remember(points) {
        CartesianLayerRangeProvider.fixed(
            minX = 0.0,
            maxX = points.lastIndex.toDouble(),
            minY = minY,
            maxY = maxY
        )
    }

    val lineColor = MaterialTheme.colorScheme.primary
    val lineFill = remember(lineColor) { LineCartesianLayer.LineFill.single(Fill(lineColor)) }
    val lineStroke = remember { LineCartesianLayer.LineStroke.Continuous(thickness = 3.5.dp) }
    val line = LineCartesianLayer.rememberLine(fill = lineFill, stroke = lineStroke, areaFill = null)
    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(line),
        rangeProvider = rangeProvider
    )

    val labelIndexes = remember(points, sample.labelSpacing) {
        points.indices.filter { it % sample.labelSpacing == 0 }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().height(CHART_HEIGHT)) {
            Column(
                modifier = Modifier.width(Y_AXIS_WIDTH).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                for (i in 0..3) {
                    val value = maxY - (maxY - minY) * i / 3
                    Text(
                        text = String.format(Locale.US, "%.4f", value),
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    lineLayer,
                    layerPadding = {
                        CartesianLayerPadding(scalableStart = EDGE_PADDING, scalableEnd = EDGE_PADDING)
                    }
                ),
                modelProducer = modelProducer,
                scrollState = rememberVicoScrollState(scrollEnabled = false),
                zoomState = rememberVicoZoomState(zoomEnabled = false),
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.width(Y_AXIS_WIDTH))
            Row(
                modifier = Modifier.weight(1f).padding(horizontal = EDGE_PADDING),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labelIndexes.forEach { index ->
                    Text(
                        text = formatAxisDate(points[index].date, selectedPreset),
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}