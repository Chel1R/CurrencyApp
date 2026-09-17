package com.khyzhniak.currencyapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.khyzhniak.currencyapp.domain.repository.CurrencyRepository
import jakarta.inject.Inject

class ChartViewModel @Inject constructor(
    private val repository: CurrencyRepository
): ViewModel() {

}