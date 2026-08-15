package com.khyzhniak.currencyapp.data.model

data class HistoryRates(
    val date: String,
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double
)
