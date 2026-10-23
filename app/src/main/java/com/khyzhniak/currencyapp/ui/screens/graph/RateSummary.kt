package com.khyzhniak.currencyapp.ui.screens.graph

import com.khyzhniak.currencyapp.domain.model.HistoryRates

data class RateSummary(
    val latestRate: Double,
    val percentChange: Double,
    val absoluteChange: Double
)

fun calculateRateSummary(historyRates: List<HistoryRates>): RateSummary? {
    if (historyRates.isEmpty()) return null
    val latest = historyRates.last().rate
    val first = historyRates.first().rate
    val diff = latest - first
    val percent = if (first != 0.0) (diff / first) * 100 else 0.0

    return RateSummary(
        latestRate = latest,
        percentChange = percent,
        absoluteChange = diff
    )
}
