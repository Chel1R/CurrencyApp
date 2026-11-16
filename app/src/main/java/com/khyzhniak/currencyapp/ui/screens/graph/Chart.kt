package com.khyzhniak.currencyapp.ui.screens.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.khyzhniak.currencyapp.domain.model.DateRangePreset
import com.khyzhniak.currencyapp.domain.model.HistoryRates
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill

import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
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

private val monthYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.US)
private val monthDayFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)

private data class ChartSample(
    val points: List<HistoryRates>,
    val labelSpacing: Int,
)

private fun formatAxisDate(dateString: String, preset: DateRangePreset): String =
    try {
        val formatter = when (preset) {
            DateRangePreset.ONE_YEAR, DateRangePreset.FIVE_YEARS -> monthYearFormatter
            else -> monthDayFormatter
        }
        LocalDate.parse(dateString).format(formatter)
    } catch (e: DateTimeParseException) {
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

@Composable
fun CurrencyVicoChart(
    historyRates: List<HistoryRates>,
    selectedPreset: DateRangePreset,
    modifier: Modifier = Modifier
) {
    if (historyRates.isEmpty()) return

    val sample = remember(historyRates) { sampledForChart(historyRates) }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(sample) {
        modelProducer.runTransaction {
            lineSeries { series(sample.points.map { it.rate }) }
        }
    }

    val xAxisFormatter = remember(sample, selectedPreset) {
        CartesianValueFormatter { value, _, _ ->
            val index = value.roundToInt().coerceIn(0, sample.points.lastIndex)
            formatAxisDate(sample.points[index].date, selectedPreset)
        }
    }

    val yAxisFormatter = remember {
        CartesianValueFormatter { value, _, _ ->
            String.format(Locale.US, "%.4f", value)
        }
    }

    val axisValueOverrider = remember(sample) {
        object : AxisValueOverrider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
                sample.points.minOf { it.rate } * 0.995

            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
                sample.points.maxOf { it.rate } * 1.005
        }
    }

    val axisLabelComponent = rememberAxisLabelComponent(
        color = MaterialTheme.colorScheme.onSurface,
        textSize = 9.sp
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(MaterialTheme.colorScheme.primary)),
                        thickness = 3.5.dp,
                        areaFill = null
                    )
                ),
                axisValueOverrider = axisValueOverrider
            ),
            startAxis = rememberStartAxis(
                label = axisLabelComponent,
                valueFormatter = yAxisFormatter,
                itemPlacer = remember { VerticalAxis.ItemPlacer.count(count = { 4 }) }
            ),
            bottomAxis = rememberBottomAxis(
                label = axisLabelComponent,
                valueFormatter = xAxisFormatter,
                itemPlacer = remember(sample.labelSpacing) {
                    HorizontalAxis.ItemPlacer.default(
                        spacing = sample.labelSpacing,
                        addExtremeLabelPadding = false
                    )
                }
            ),
//            horizontalLayout = remember { HorizontalLayout.fullWidth() }
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
        modifier = modifier.fillMaxWidth().height(280.dp)
    )
}