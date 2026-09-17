package com.khyzhniak.currencyapp.ui.screens.GraphScreen

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
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.ui.components.RatesBottomSheet
import com.khyzhniak.currencyapp.ui.navigation.Screen
import com.khyzhniak.currencyapp.ui.viewmodels.CurrencyUiState
import com.khyzhniak.currencyapp.utils.toCountryFlag

@Composable
fun GraphScreen(
    uiState: CurrencyUiState,
    fromCurrency: String,
    toCurrency: String,
    currencyList: List<String>,
    onToCurrencyChange: (String) -> Unit,
    onFromCurrencyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
//    isObsolete: Boolean,
){
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit)
            {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        contentAlignment = Alignment.Center
    ){
        when(uiState){
            is CurrencyUiState.Loading -> {
                CircularProgressIndicator()
            }
            is CurrencyUiState.Error ->{

            }
            is CurrencyUiState.Success -> {
                Column(
                    Modifier.fillMaxSize()
                        .padding()
                        .verticalScroll(scrollState)
                        .padding(8.dp),
                ) {
                    var showBottomSheet by rememberSaveable() { mutableStateOf(false) }
                    var isFromSelected by rememberSaveable() { mutableStateOf(true) }

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

@Preview
@Composable
fun testGraph(){
    GraphCard(
        fromCurrency = "UAH",
        toCurrency = "EUR",
        onFromCurrencyClick = {},
        onToCurrencyClick = {},
    )
}

@Composable
fun GraphCard(
    fromCurrency : String,
    toCurrency : String,
    onFromCurrencyClick: () -> Unit,
    onToCurrencyClick: () -> Unit,
//    onFromCurrencyChange: (String) -> Unit,
//    onToCurrencyChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth().padding(8.dp),
            shape = MaterialTheme.shapes.small,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
           CurrencySelectionSection(
               onFromCurrencyClick = onFromCurrencyClick,
               onToCurrencyClick = onToCurrencyClick,
               fromCurrency = fromCurrency,
               toCurrency = toCurrency
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
)
{
    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
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
    title : String,
    modifier: Modifier = Modifier,
){
 Column(
     modifier = modifier.fillMaxWidth()
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