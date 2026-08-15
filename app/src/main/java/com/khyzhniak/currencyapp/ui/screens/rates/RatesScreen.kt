package com.khyzhniak.currencyapp.ui.screens.rates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.R
import com.khyzhniak.currencyapp.ui.components.CustomTextField
import com.khyzhniak.currencyapp.ui.components.RatesBottomSheet
import com.khyzhniak.currencyapp.ui.viewmodels.ConversionUiState
import com.khyzhniak.currencyapp.ui.viewmodels.CurrencyUiState
import com.khyzhniak.currencyapp.ui.viewmodels.RefreshState
import com.khyzhniak.currencyapp.utils.toCountryFlag


@Preview(showBackground = true)
@Composable
fun test() {
    RatesScreen(
        uiState = CurrencyUiState.Success,
        onFromAmountChange = { },
        currencyList = listOf("USD,", "UAH", "EUR"),
        fromCurrency = "USD",
        onFromCurrencyChange = {},
        toCurrency = "UAH",
        onToCurrencyChange = {},
        onToResultChange = {},
        onSwapClick = {},
        mainCurrency = "TODO()",
        popularCurrencyList = emptyMap(),
        calculatedMapForTextFields = ConversionUiState(),
        isObsolete = true,
        refreshState = RefreshState.Idle,
        onRefreshClick = { },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesScreen(
    uiState: CurrencyUiState,
    fromCurrency: String,
    toCurrency: String,
    currencyList: List<String>,
    onFromAmountChange: (String) -> Unit,
    onToResultChange: (String) -> Unit,
    onFromCurrencyChange: (String) -> Unit,
    onToCurrencyChange: (String) -> Unit,
    onSwapClick: () -> Unit,
    mainCurrency: String,
    popularCurrencyList: Map<String, String>,
    calculatedMapForTextFields: ConversionUiState,
    isObsolete: Boolean,
    refreshState: RefreshState,
    onRefreshClick: () -> Unit
) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Surface(shadowElevation = 8.dp) {
                TopAppBar(title = {
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        text = stringResource(R.string.currency_header_title)
                    )
                })
            }
        },
        bottomBar = {},
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit)
                {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is CurrencyUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CurrencyUiState.Error -> {
                    ErrorCard(
                        onRefreshClick = onRefreshClick,
                        enabled = refreshState !is RefreshState.Refreshing,
                        errorMessage = stringResource(R.string.error_message),
                        actionDescription = "Retry",
                        modifier = Modifier.align(Alignment.TopCenter)
                        .padding(16.dp)
                    )
                }

                is CurrencyUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    )
                    {

                        if (isObsolete) {
                            ErrorCard(
                                onRefreshClick,
                                enabled = refreshState !is RefreshState.Refreshing,
                                errorMessage = stringResource(R.string.obsolete_error_message),
                                actionDescription = "Refresh",
                            )
                        }

                        if (refreshState is RefreshState.Refreshing) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        var showBottomSheet by rememberSaveable() { mutableStateOf(false) }
                        var isFromSelected by rememberSaveable() { mutableStateOf(true) }

                        CurrencyConverterCard(
                            fromAmountText = calculatedMapForTextFields.fromAmount,
                            toAmountText = calculatedMapForTextFields.toAmount,
                            fromCurrency = fromCurrency,
                            toCurrency = toCurrency,
                            onFromAmountChange = onFromAmountChange,
                            onFromCurrencyClick = {
                                showBottomSheet = true
                                isFromSelected = true
                            },
                            onToCurrencyClick = {
                                showBottomSheet = true
                                isFromSelected = false
                            },
                            onToResultChange = onToResultChange,
                            onSwapClick = onSwapClick
                        )

                        RatesListCard(popularCurrencyList, mainCurrency)
                        if (showBottomSheet) {
                            RatesBottomSheet(
                                currencyList = currencyList,
                                onDismissRequest = { showBottomSheet = false },
                                onCurrencySelected = { selectedCurrency ->
                                    if (isFromSelected) {
                                        onFromCurrencyChange(selectedCurrency)
                                    } else {
                                        onToCurrencyChange(selectedCurrency)
                                    }
                                }

                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ErrorPreview() {
    ErrorCard({}, false)
}

@Composable
fun BannerActionRow(
    message: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionDescription: String = "Refresh",
    textColor: Color = Color.Unspecified,
    buttonColors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        IconButton(
            enabled = enabled,
            onClick = onActionClick,
            colors = buttonColors
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = actionDescription,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun ErrorCard(
    onRefreshClick: () -> Unit,
    enabled: Boolean,
    errorMessage: String = stringResource(R.string.error_message),
    actionDescription: String = "Refresh",
    modifier: Modifier = Modifier,
){
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = MaterialTheme.shapes.small
    ) {
        BannerActionRow(
            message = errorMessage,
            actionDescription = actionDescription,
            onActionClick = onRefreshClick,
            textColor = MaterialTheme.colorScheme.onErrorContainer,
            buttonColors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            enabled = enabled

        )
    }
}