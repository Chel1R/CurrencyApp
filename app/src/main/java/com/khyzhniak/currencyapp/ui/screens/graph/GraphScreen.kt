package com.khyzhniak.currencyapp.ui.screens.graph

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.domain.model.DateRangePreset
import com.khyzhniak.currencyapp.domain.model.HistoryRates
import com.khyzhniak.currencyapp.ui.components.RatesBottomSheet
import com.khyzhniak.currencyapp.ui.screens.rates.ErrorCard
import com.khyzhniak.currencyapp.ui.viewmodels.ChartUiState
import com.khyzhniak.currencyapp.ui.viewmodels.RefreshChartState
import com.khyzhniak.currencyapp.utils.toCountryFlag

@Preview
@Composable
fun GraphScreenPreview() {
    GraphScreen(
        uiState = ChartUiState.Success,
        fromCurrency = "UAH",
        toCurrency = "EUR",
        currencyList = listOf("UAH", "EUR", "USD", "PLN"),
        onToCurrencyChange = {},
        onFromCurrencyChange = {},
        refreshState = RefreshChartState.Idle,
        selectedPreset = DateRangePreset.ONE_WEEK,
        onDataRangePresetChange = { },
        historyRates = listOf()
    )
}

@Composable
fun GraphScreen(
    uiState: ChartUiState,
    fromCurrency: String,
    toCurrency: String,
    currencyList: List<String>,
    historyRates: List<HistoryRates>,
    onToCurrencyChange: (String) -> Unit,
    onFromCurrencyChange: (String) -> Unit,
    refreshState: RefreshChartState,
    modifier: Modifier = Modifier,
    selectedPreset: DateRangePreset,
    onDataRangePresetChange: (DateRangePreset) -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var isFromSelected by rememberSaveable { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is ChartUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ChartUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ErrorCard(
                        onRefreshClick = { onFromCurrencyChange(fromCurrency) },
                        enabled = refreshState !is RefreshChartState.Refreshing,
                        errorMessage = uiState.message,
                        actionDescription = "Retry"
                    )
                    GraphCard(
                        fromCurrency = fromCurrency,
                        toCurrency = toCurrency,
                        onFromCurrencyClick = {
                            isFromSelected = true
                            showBottomSheet = true
                        },
                        onToCurrencyClick = {
                            isFromSelected = false
                            showBottomSheet = true
                        },
                        selectedPreset = selectedPreset,
                        onDataRangePresetChange = onDataRangePresetChange,
                        historyRates = historyRates
                    )
                }
            }

            is ChartUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (refreshState is RefreshChartState.Refreshing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    GraphCard(
                        fromCurrency = fromCurrency,
                        toCurrency = toCurrency,
                        onFromCurrencyClick = {
                            isFromSelected = true
                            showBottomSheet = true
                        },
                        onToCurrencyClick = {
                            isFromSelected = false
                            showBottomSheet = true
                        },
                        selectedPreset = selectedPreset,
                        onDataRangePresetChange = onDataRangePresetChange,
                        historyRates = historyRates,
                    )
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

@Composable
fun GraphCard(
    fromCurrency: String,
    toCurrency: String,
    historyRates: List<HistoryRates>,
    onFromCurrencyClick: () -> Unit,
    onToCurrencyClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedPreset: DateRangePreset,
    onDataRangePresetChange: (DateRangePreset) -> Unit
) {
    val ratesSummary = remember(historyRates) { calculateRateSummary(historyRates) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Chart",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            CurrencySelectionSection(
                onFromCurrencyClick = onFromCurrencyClick,
                onToCurrencyClick = onToCurrencyClick,
                fromCurrency = fromCurrency,
                toCurrency = toCurrency
            )
            PeriodSelection(
                selectedPreset = selectedPreset,
                onDataRangePresetChange = onDataRangePresetChange,
            )
            if (ratesSummary != null) {
                RateSummaryHeader(
                    fromCurrency = fromCurrency,
                    toCurrency = toCurrency,
                    latestRate = ratesSummary.latestRate,
                    percentChange = ratesSummary.percentChange,
                    absoluteChange = ratesSummary.absoluteChange,
                )
            }
            CurrencyVicoChart(historyRates = historyRates, selectedPreset = selectedPreset)
        }
    }
}

@Composable
fun PeriodSelection(
    selectedPreset: DateRangePreset,
    onDataRangePresetChange: (DateRangePreset) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DateRangePreset.entries.forEach { preset ->
            FilterChip(
                modifier = Modifier.weight(1f),
                selected = (preset == selectedPreset),
                onClick = { onDataRangePresetChange(preset) },
                label = {
                    Text(
                        text = preset.label,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}

@Composable
fun CurrencySelectionSection(
    onFromCurrencyClick: () -> Unit,
    onToCurrencyClick: () -> Unit,
    fromCurrency: String,
    toCurrency: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CurrencySelectorBlock(
            onCurrencyClick = onFromCurrencyClick,
            currency = fromCurrency,
            title = "Base",
            modifier = Modifier.weight(1f)
        )
        CurrencySelectorBlock(
            onCurrencyClick = onToCurrencyClick,
            currency = toCurrency,
            title = "Target",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CurrencySelectorBlock(
    onCurrencyClick: () -> Unit,
    currency: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            onClick = onCurrencyClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currency.toCountryFlag(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = currency,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
