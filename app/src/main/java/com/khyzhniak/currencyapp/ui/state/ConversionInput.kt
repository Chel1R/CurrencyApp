package com.khyzhniak.currencyapp.ui.state

import kotlinx.coroutines.flow.MutableStateFlow

data class ConversionInput(
    val fromAmount: String,
    val toAmount: String,
    val fromCurrency: String,
    val toCurrency: String
)
