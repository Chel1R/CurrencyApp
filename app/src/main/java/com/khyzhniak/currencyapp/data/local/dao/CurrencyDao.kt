package com.khyzhniak.currencyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

import com.khyzhniak.currencyapp.data.local.entity.CurrencyRatesTable
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Upsert
    suspend fun saveRates(rates : List<CurrencyRatesTable>)

    @Query("SELECT * From current_rates")
    fun getRates() : Flow<List<CurrencyRatesTable>>


}

