package com.khyzhniak.currencyapp.domain.repository

import com.khyzhniak.currencyapp.data.local.entity.CurrencyHistory
import com.khyzhniak.currencyapp.data.model.CurrencyRates
import com.khyzhniak.currencyapp.data.model.DateRange
import com.khyzhniak.currencyapp.data.model.HistoryGroup
import com.khyzhniak.currencyapp.data.model.HistoryRates
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    fun observeRates(): Flow<List<CurrencyRates>>
    suspend fun refreshRates()
    fun observeHistoryRates(): Flow<List<HistoryRates>>

    suspend fun refreshHistoryRates(
        base : String,
        quotes : String,
        dateRange : DateRange,
        group : HistoryGroup
    )
}