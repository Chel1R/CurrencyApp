package com.khyzhniak.currencyapp.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.khyzhniak.currencyapp.R
import com.khyzhniak.currencyapp.ui.screens.rates.RatesScreen
import com.khyzhniak.currencyapp.ui.viewmodels.CurrencyViewModel
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable object RatesScreen : Screen
    @Serializable object GraphScreen : Screen
}

data class BottomNavItem(
    val label : String,
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon : ImageVector,
    val contentDescription : String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController){

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = backStackEntry?.destination

    val showBottomBar = when{
        currentDestination?.hasRoute<Screen.RatesScreen>() == true -> true
        else -> false
    }

    val topBarText : String? = when{
        currentDestination?.hasRoute<Screen.RatesScreen>() == true -> stringResource(R.string.currency_header_title)
        else -> null
    }


    Scaffold(
            topBar = {
                topBarText?.let{ text ->
                Surface(shadowElevation = 8.dp) {
                    TopAppBar(title = {
                        Text(
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            text = text
                        )
                    })
                }
            }},
        bottomBar = {

            val items = listOf(
                BottomNavItem(
                    label = "Rates",
                    route = Screen.RatesScreen,
                    selectedIcon = Icons.Filled.CurrencyExchange,
                    unselectedIcon = Icons.Outlined.CurrencyExchange,
                    contentDescription = ""
                ),
                BottomNavItem(
                    label = "Chart",
                    route = Screen.GraphScreen,
                    selectedIcon = Icons.AutoMirrored.Filled.ShowChart,
                    unselectedIcon = Icons.AutoMirrored.Outlined.ShowChart,
                    contentDescription = ""
                )

            )

            if (showBottomBar){
                NavigationBar (
                    modifier = Modifier.height(110.dp),
                    tonalElevation = 0.dp)
                {
//                    NavigationBarItem(
//                        selected = currentDestination?.hasRoute<Screen.RatesScreen>() == true,
//                        onClick = {
//                            navController.navigate(Screen.RatesScreen)
//                        },
//                        icon = {
//
//                        },
//                        alwaysShowLabel = false,
//                    )
//                    NavigationBarItem(
//                        selected = currentDestination?.hasRoute<Screen.GraphScreen>() == true,
//                        onClick = {
//                            navController.navigate(Screen.GraphScreen)
//                        },
//                        icon = {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.ShowChart,
//                                contentDescription = "Currency exchange show chart screen"
//                            )
//                        },
//                        alwaysShowLabel = false,
//                    )

                    items.forEach {
                        item ->
                        val isSelected = currentDestination?.hierarchy?.any{
                            it.hasRoute(item.route::class)} == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route){
                                    popUpTo(navController.graph.findStartDestination().id){
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.contentDescription
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    )
    { innerPadding ->
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
                    modifier = Modifier.padding(innerPadding)
                )
            }

            composable<Screen.GraphScreen>{

            }
        }
    }
}