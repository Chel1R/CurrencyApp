package com.khyzhniak.currencyapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyRate(
    val base : String,
    @Json(name = "quote") val targetCurrency: String,
    val date: String,
    val rate: Double
)
