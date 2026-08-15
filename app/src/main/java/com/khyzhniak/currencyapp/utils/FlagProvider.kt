package com.khyzhniak.currencyapp.utils

fun String.toCountryFlag(): String {
    return try {

        val countryCode = this.take(2).uppercase()


        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6


        String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    } catch (e: Exception) {
        "🌐"
    }
}