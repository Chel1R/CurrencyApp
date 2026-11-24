package com.khyzhniak.currencyapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khyzhniak.currencyapp.domain.model.ThemeMode
import com.khyzhniak.currencyapp.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = userSettingsRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.SYSTEM_THEME,
    )

    val mainCurrency: StateFlow<String> = userSettingsRepository.mainCurrency.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "UAH",
    )

    fun onThemeModeSelected(theme: ThemeMode) {
        viewModelScope.launch {
            userSettingsRepository.saveThemeMode(theme)
        }
    }

    fun onMainCurrencySelected(currency: String) {
        viewModelScope.launch {
            userSettingsRepository.saveMainCurrency(currency)
        }
    }
}
