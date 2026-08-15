package com.khyzhniak.currencyapp.data.remote.dto

import com.squareup.moshi.Json

data class CurrencyRateInRange(
    val date: String,
    val base: String,
    @Json(name = "quote") val targetCurrency: String,
    val rate: Double
)
