package com.khyzhniak.currencyapp.ui.screens.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill
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
import java.util.Locale

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
    val changeColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
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
            text = "$arrow ${String.format(Locale.US, "%.2f", percentChange)}% (${if (isPositive) "+" else ""}${String.format(Locale.US, "%.4f", absoluteChange)})",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = changeColor
        )
    }
}

@Composable
fun CurrencyVicoChart(
    historyRates: List<HistoryRates>,
    selectedPreset: DateRangePreset,
    modifier: Modifier = Modifier
) {
    if (historyRates.isEmpty()) return

    val sampledRates = remember(historyRates) {
        val sorted = historyRates.sortedBy { it.date }
        if (sorted.size > 50) {
            val step = sorted.size.toDouble() / 50
            (0 until 50).map { i ->
                sorted[(i * step).toInt().coerceAtMost(sorted.lastIndex)]
            }
        } else {
            sorted
        }
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(sampledRates) {
        modelProducer.runTransaction {
            lineSeries {
                series(sampledRates.map { it.rate })
            }
        }
    }

    val xAxisFormatter = CartesianValueFormatter { value, _, _ ->
        val index = value.toInt().coerceIn(0, sampledRates.lastIndex)
        val dateStr = sampledRates.getOrNull(index)?.date ?: ""
        try {
            val date = LocalDate.parse(dateStr)
            if (selectedPreset == DateRangePreset.ONE_YEAR || selectedPreset == DateRangePreset.FIVE_YEARS) {
                date.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.US))
            } else {
                date.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
            }
        } catch (e: Exception) {
            dateStr
        }
    }

    val yAxisFormatter = CartesianValueFormatter { value, _, _ ->
        String.format(Locale.US, "%.4f", value)
    }

    val axisValueOverrider = remember(sampledRates) {
        object : AxisValueOverrider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                val min = sampledRates.minOfOrNull { it.rate } ?: minY
                return min * 0.995
            }
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                val max = sampledRates.maxOfOrNull { it.rate } ?: maxY
                return max * 1.005
            }
        }
    }

    val axisLabelComponent = rememberAxisLabelComponent(
        color = MaterialTheme.colorScheme.onSurface,
        textSize = 10.sp
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    rememberLine(
                        fill = LineCartesianLayer.LineFill.single(
                            fill(MaterialTheme.colorScheme.primary)
                        ),
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
                itemPlacer = remember { HorizontalAxis.ItemPlacer.default(spacing = 3) }
            )
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 20.dp)
    )
}
