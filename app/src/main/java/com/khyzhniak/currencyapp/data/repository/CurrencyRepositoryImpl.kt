package com.khyzhniak.currencyapp.data.repository

import com.khyzhniak.currencyapp.data.local.dao.CurrencyDao
import com.khyzhniak.currencyapp.data.local.dao.HistoryDao
import com.khyzhniak.currencyapp.data.local.mapper.toApiDateString
import com.khyzhniak.currencyapp.data.local.mapper.toApiGroupValue
import com.khyzhniak.currencyapp.data.remote.ApiService
import com.khyzhniak.currencyapp.domain.repository.CurrencyRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import com.khyzhniak.currencyapp.data.local.mapper.toEntity
import com.khyzhniak.currencyapp.data.local.mapper.toEntityHistory
import com.khyzhniak.currencyapp.data.local.mapper.toRate
import com.khyzhniak.currencyapp.data.local.mapper.toRateHistory
import kotlinx.coroutines.flow.map
import com.khyzhniak.currencyapp.data.model.CurrencyRates
import com.khyzhniak.currencyapp.data.model.DateRange
import com.khyzhniak.currencyapp.data.model.HistoryGroup
import com.khyzhniak.currencyapp.data.model.HistoryRates

class CurrencyRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: CurrencyDao,
    private val daoHistory: HistoryDao

) : CurrencyRepository {

    override fun observeRates(): Flow<List<CurrencyRates>> {
        return dao.getRates().map { entities -> entities.map { it.toRate() } }
    }


    override suspend fun refreshRates() {
        val response = api.getAllRates()
        dao.saveRates(response.map { it.toEntity() })
    }

    override fun observeHistoryRates(): Flow<List<HistoryRates>> {
        return daoHistory.loadHistory().map { entities -> entities.map { it.toRateHistory() } }
    }

    override suspend fun refreshHistoryRates(
        base: String,
        quotes: String,
        dateRange : DateRange,
        group : HistoryGroup
    ) {
        val response = api.getSpecificRateInRange(base, quotes, dateRange.from.toApiDateString(), dateRange.to.toApiDateString(), group.toApiGroupValue())
        daoHistory.saveHistory(response.map { it.toEntityHistory() })
    }

}
