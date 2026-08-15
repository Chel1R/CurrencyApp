package com.khyzhniak.currencyapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.khyzhniak.currencyapp.data.model.CurrencyRates
import com.khyzhniak.currencyapp.domain.repository.CurrencyRepository
import com.khyzhniak.currencyapp.ui.state.ConversionInput
import com.khyzhniak.currencyapp.ui.theme.CurrencyAppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.collections.associate

sealed class CurrencyUiState {
    object Loading : CurrencyUiState()
    object Success : CurrencyUiState()
    data class Error(val message: String?) : CurrencyUiState()
}

sealed interface RefreshState {
    object Idle : RefreshState
    object Refreshing : RefreshState
    data class Error(val message: String) : RefreshState
}

data class ConversionUiState(
    val fromAmount: String = "",
    val toAmount: String = ""
)

enum class ConvertDirection { FROM, TO }

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {


    //Refresh state for handling the refresh of the data
    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Idle)
    val refreshState = _refreshState.asStateFlow()

    private val _isObsolete = MutableStateFlow(false)
    val isObsolete = _isObsolete.asStateFlow()
    val convertedDirection = MutableStateFlow(ConvertDirection.FROM)
    val fromAmountState = MutableStateFlow("1")
    val fromCurrency = MutableStateFlow("USD")
    val toCurrency = MutableStateFlow("UAH")
    val toAmountState = MutableStateFlow("")

    val mainCurrency = MutableStateFlow("UAH")

    val popularCurrencyRates =
        MutableStateFlow(listOf("EUR", "USD", "GBP", "PLN", "CHF", "AUD"))

    private val _rates = MutableStateFlow<List<CurrencyRates>>(emptyList())
    val rates = _rates.asStateFlow()


    val uiState : StateFlow<CurrencyUiState> = combine(
        _rates,
        _refreshState
    ){
            rates, refresh ->
        when{
            rates.isNotEmpty() -> CurrencyUiState.Success
            refresh is RefreshState.Error -> CurrencyUiState.Error(refresh.message)
            else -> CurrencyUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CurrencyUiState.Loading,
    )
    private val ratesMap = rates.map { rates ->
        rates.associate { it.code to it.rate }

    }
    val calculatedPopularCurrencies: StateFlow<Map<String, String>> = combine(
        popularCurrencyRates,
        mainCurrency,
        ratesMap
    ) { codes, main, rates ->
        codes.associate { targetCurrency ->
            targetCurrency to calculateRates(
                "1",
                main,
                targetCurrency,
                rates
            )
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )


    fun onAmountChange(newAmount: String) {
        fromAmountState.value = newAmount
        convertedDirection.value = ConvertDirection.FROM
    }

    fun onResultChange(newResult: String) {
        toAmountState.value = newResult
        convertedDirection.value = ConvertDirection.TO
    }

    fun onFromCurrencyChange(newFromCurrency: String) {
        fromCurrency.value = newFromCurrency
    }

    fun onToCurrencyChange(newToCurrency: String) {
        toCurrency.value = newToCurrency
    }

    fun onSwap() {
        val tempCurrency = fromCurrency.value
        fromCurrency.value = toCurrency.value
        toCurrency.value = tempCurrency

        convertedDirection.value = ConvertDirection.FROM
    }


    private val conversionInput = combine(
        fromAmountState,
        toAmountState,
        fromCurrency,
        toCurrency,
    ) { fromAmount, toAmount, from, to ->
        ConversionInput(
            fromAmount = fromAmount,
            toAmount = toAmount,
            fromCurrency = from,
            toCurrency = to,
        )
    }

    val conversionResult = combine(
        conversionInput,
        convertedDirection,
        ratesMap
    ) { conversionState, direction, ratesMap ->


        when (direction) {
            ConvertDirection.FROM ->
                ConversionUiState(
                    fromAmount = conversionState.fromAmount,
                    toAmount = calculateRates(
                        amountStr = conversionState.fromAmount,
                        fromCurrency = conversionState.fromCurrency,
                        toCurrency = conversionState.toCurrency,
                        rates = ratesMap
                    )
                )

            ConvertDirection.TO ->
                ConversionUiState(
                    fromAmount = calculateRates(
                        amountStr = conversionState.toAmount,
                        fromCurrency = conversionState.toCurrency,
                        toCurrency = conversionState.fromCurrency,
                        rates = ratesMap
                    ),
                    toAmount = conversionState.toAmount
                )
        }

    }.stateIn(
        initialValue = ConversionUiState(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    private fun calculateRates(
        amountStr: String,
        fromCurrency: String,
        toCurrency: String,
        rates: Map<String, Double>
    ): String {
        val doubleAmount = amountStr.toDoubleOrNull() ?: return ""

        val fromRate = if (fromCurrency == "EUR") 1.0 else rates[fromCurrency] ?: return ""
        val toRate = if (toCurrency == "EUR") 1.0 else rates[toCurrency] ?: return ""

        val result = (doubleAmount / fromRate) * toRate
        return String.format(Locale.US, "%.2f", result)
    }

    init {
        viewModelScope.launch {
            observeRates()
        }
        refreshRates()
    }

    private suspend fun observeRates() {
        repository.observeRates().collect { newRates ->
            _rates.value = newRates
        }
    }

    fun refreshRates() {
        viewModelScope.launch {
            _refreshState.value = RefreshState.Refreshing
            try {
                repository.refreshRates()
                _refreshState.value = RefreshState.Idle
                _isObsolete.value = false
            } catch (e: Exception) {
                _refreshState.value = RefreshState.Error("Couldn't refresh rates")

                if (_rates.value.isEmpty()) {
                    val cached = withTimeoutOrNull(2000) {
                        repository.observeRates().first()
                    }
                    if (!cached.isNullOrEmpty()) {
                        _rates.value = cached
                    }
                }


                _isObsolete.value = _rates.value.isNotEmpty()
            }
        }
    }


}


