package com.khyzhniak.currencyapp.di

import android.content.Context
import androidx.room.Room

import com.khyzhniak.currencyapp.data.local.CurrencyDataBase
import com.khyzhniak.currencyapp.data.local.dao.CurrencyDao
import com.khyzhniak.currencyapp.data.local.dao.HistoryDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ): CurrencyDataBase
    {
        return Room.databaseBuilder(
            context,
            CurrencyDataBase::class.java,
            "CurrencyDataBase"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(db : CurrencyDataBase): CurrencyDao{
        return db.currencyDao()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(db : CurrencyDataBase): HistoryDao{
        return db.historyDao()
    }

}