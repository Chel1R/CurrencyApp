package com.khyzhniak.currencyapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.khyzhniak.currencyapp.domain.model.ThemeMode
import com.khyzhniak.currencyapp.domain.repository.UserSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserSettingsRepository {

    companion object {
        private val KEY_MAIN_CURRENCY = stringPreferencesKey("main_currency")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }

    override val mainCurrency: Flow<String> = context.settingsStore.data.map { preferences ->
        preferences[KEY_MAIN_CURRENCY] ?: "UAH"
    }

    override suspend fun saveMainCurrency(currency: String) {
        context.settingsStore.edit { preferences ->
            preferences[KEY_MAIN_CURRENCY] = currency
        }
    }

    override val themeMode: Flow<ThemeMode> = context.settingsStore.data.map { preferences ->
        val savedName = preferences[KEY_THEME_MODE] ?: ThemeMode.SYSTEM_THEME.name
        try {
            ThemeMode.valueOf(savedName)
        } catch (e: Exception) {
            ThemeMode.SYSTEM_THEME
        }
    }

    override suspend fun saveThemeMode(theme: ThemeMode) {
        context.settingsStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = theme.name
        }
    }
}
