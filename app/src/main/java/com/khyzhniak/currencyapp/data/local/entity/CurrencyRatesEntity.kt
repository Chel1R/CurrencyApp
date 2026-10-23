package com.khyzhniak.currencyapp.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("current_rates")
data class CurrencyRatesTable (
    @PrimaryKey val code: String,
    val date: String,
    val base : String,
    val rateToEur: Double
)
