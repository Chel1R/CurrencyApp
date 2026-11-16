package com.khyzhniak.currencyapp.di

import com.khyzhniak.currencyapp.data.repository.CurrencyRepositoryImpl
import com.khyzhniak.currencyapp.data.repository.UserSettingsRepositoryImpl
import com.khyzhniak.currencyapp.domain.repository.CurrencyRepository
import com.khyzhniak.currencyapp.domain.repository.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: UserSettingsRepositoryImpl
    ): UserSettingsRepository
}