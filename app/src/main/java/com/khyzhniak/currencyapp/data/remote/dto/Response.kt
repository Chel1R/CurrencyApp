package com.khyzhniak.currencyapp.data.remote.dto


//Probably redundant
data class Response(
    val base: String,
    val date: String,
    val rates: Map<String,Double>
)