package com.khyzhniak.currencyapp.domain.model

data class CurrencyRates (
    val base : String,
    val code : String,
    val rate : Double,
    val date : String
)

