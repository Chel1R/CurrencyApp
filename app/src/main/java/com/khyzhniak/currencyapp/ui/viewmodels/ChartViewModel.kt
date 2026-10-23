package com.khyzhniak.currencyapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khyzhniak.currencyapp.domain.model.DateRangePreset
import com.khyzhniak.currencyapp.domain.model.DateRange
import com.khyzhniak.currencyapp.domain.model.HistoryRates
import com.khyzhniak.currencyapp.domain.repository.CurrencyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import com.khyzhniak.currencyapp.utils.convertToGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.emptyList

sealed class ChartUiState{
    object Loading : ChartUiState()
    object Success : ChartUiState()
    data class Error(
        val message: String
    ) : ChartUiState()
}

sealed class RefreshChartState{
    object Idle : RefreshChartState()
    object Refreshing : RefreshChartState()
    data class Error(
        val message: String
    ) : RefreshChartState()
}

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: CurrencyRepository
): ViewModel() {

    private var _refreshState = MutableStateFlow<RefreshChartState>(RefreshChartState.Idle)
    val refreshState = _refreshState.asStateFlow()

    private val _fromCurrency = MutableStateFlow("UAH")
    val fromCurrency = _fromCurrency.asStateFlow()

    private val _toCurrency = MutableStateFlow("USD")
    val toCurrency = _toCurrency.asStateFlow()

    fun onFromChartCurrencyChange(newFromCurrency : String){
        _fromCurrency.value = newFromCurrency
        refreshHistoryRates()
    }

    fun onToChartCurrencyChange(newToCurrency : String){
        _toCurrency.value = newToCurrency
        refreshHistoryRates()
    }

    private val _dateRangePreset = MutableStateFlow(DateRangePreset.ONE_MONTH)
    val dateRangePreset = _dateRangePreset.asStateFlow()

    fun onDataRangePresetChange(preset: DateRangePreset){
        _dateRangePreset.value = preset
        refreshHistoryRates()
    }

    val availableCurrencies: StateFlow<List<String>> = repository.observeRates()
        .map { rates -> rates.map { it.code } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    @OptIn(ExperimentalCoroutinesApi::class)
    var historyRates : StateFlow<List<HistoryRates>> = combine(
        _fromCurrency,
        _toCurrency,
        dateRangePreset,
    ){
            base, target, preset ->
        Triple(base,target,preset)
    }.flatMapLatest{
            (base, target, preset) -> repository.observeHistoryRates(
        base = base,
        target = target,
        dateRange = preset.toDataRange(),
    )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val uiState : StateFlow<ChartUiState> = combine(
        historyRates,
        refreshState
    ){
            rates, refreshState ->
        when{
            rates.isNotEmpty() -> ChartUiState.Success
            refreshState is RefreshChartState.Error -> ChartUiState.Error(refreshState.message)
            else -> ChartUiState.Loading

        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = ChartUiState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    init {
        refreshHistoryRates()
    }

    fun refreshHistoryRates(){
        _refreshState.value = RefreshChartState.Refreshing
        viewModelScope.launch {
            try {
                repository.refreshRates()

                repository.refreshHistoryRates(
                        base = fromCurrency.value,
                        target = toCurrency.value,
                        dateRange = dateRangePreset.value.toDataRange(),
                        group = dateRangePreset.value.toDataRange().convertToGroup()
                )
                _refreshState.value = RefreshChartState.Idle
            }
            catch (e : Exception ){
                _refreshState.value = RefreshChartState.Error("Couldn't get data")
            }
        }
    }
}