package com.khyzhniak.currencyapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.khyzhniak.currencyapp.ui.screens.rates.RatesScreen
import com.khyzhniak.currencyapp.ui.viewmodels.CurrencyViewModel
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable object RatesScreen : Screen

}

@Composable
fun AppNavigation(navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.RatesScreen){
        composable<Screen.RatesScreen> {

            val viewModel :  CurrencyViewModel = hiltViewModel()

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val rates by viewModel.rates.collectAsStateWithLifecycle()

            val calculatedMapForTextFields by viewModel.conversionResult.collectAsStateWithLifecycle()

            val fromCurrency by viewModel.fromCurrency.collectAsStateWithLifecycle()
            val toCurrency by viewModel.toCurrency.collectAsStateWithLifecycle()

            val calculatedCurrencies by viewModel.calculatedPopularCurrencies.collectAsStateWithLifecycle()
            val mainCurrency by viewModel.mainCurrency.collectAsStateWithLifecycle()
            val isObsolete by viewModel.isObsolete.collectAsStateWithLifecycle()

            val refreshState by viewModel.refreshState.collectAsStateWithLifecycle()


            val currencies = rates.map { it.code }



            RatesScreen(
                toCurrency = toCurrency,
                fromCurrency = fromCurrency,
                currencyList = currencies,
                onFromAmountChange = viewModel::onAmountChange,
                onFromCurrencyChange = viewModel::onFromCurrencyChange,
                onToCurrencyChange = viewModel::onToCurrencyChange,
                onToResultChange = viewModel::onResultChange,
                uiState = uiState,
                onSwapClick = viewModel::onSwap,
                mainCurrency = mainCurrency,
                popularCurrencyList = calculatedCurrencies,
                calculatedMapForTextFields = calculatedMapForTextFields,
                isObsolete = isObsolete,
                refreshState = refreshState,
                onRefreshClick = viewModel::refreshRates,
            )
        }
    }
}