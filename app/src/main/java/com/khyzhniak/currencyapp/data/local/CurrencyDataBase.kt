package com.khyzhniak.currencyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.khyzhniak.currencyapp.data.local.dao.CurrencyDao
import com.khyzhniak.currencyapp.data.local.dao.HistoryDao
import com.khyzhniak.currencyapp.data.local.entity.CurrencyHistory
import kotlin.reflect.KClass
import com.khyzhniak.currencyapp.data.local.entity.CurrencyRatesTable


@Database(
    entities = [CurrencyRatesTable::class, CurrencyHistory::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDataBase : RoomDatabase() {
    abstract fun currencyDao() : CurrencyDao
    abstract fun historyDao() : HistoryDao
}