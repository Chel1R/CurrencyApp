package com.khyzhniak.currencyapp.utils

import java.util.Currency
import java.util.Locale

fun String.getCurrencyDisplayName(): String {
    return try {
        val currency = Currency.getInstance(this.uppercase())

        currency.getDisplayName(Locale.ENGLISH)
    } catch (e: IllegalArgumentException) {
        this
    }
}