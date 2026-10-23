package com.khyzhniak.currencyapp.domain.model

data class HistoryRates(
    val date: String,
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double
)
