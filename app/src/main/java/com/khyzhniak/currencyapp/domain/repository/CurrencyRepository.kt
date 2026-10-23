package com.khyzhniak.currencyapp.domain.repository

import com.khyzhniak.currencyapp.domain.model.CurrencyRates
import com.khyzhniak.currencyapp.domain.model.DateRange
import com.khyzhniak.currencyapp.domain.model.HistoryGroup
import com.khyzhniak.currencyapp.domain.model.HistoryRates
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    fun observeRates(): Flow<List<CurrencyRates>>
    suspend fun refreshRates()
    fun observeHistoryRates(
        base: String,
        target: String,
        dateRange: DateRange,
    ): Flow<List<HistoryRates>>

    suspend fun refreshHistoryRates(
        base : String,
        target : String,
        dateRange : DateRange,
        group : HistoryGroup
    )
}