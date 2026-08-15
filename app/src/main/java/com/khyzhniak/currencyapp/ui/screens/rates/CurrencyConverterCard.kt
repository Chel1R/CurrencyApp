package com.khyzhniak.currencyapp.ui.screens.rates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.ui.components.CustomTextField
import com.khyzhniak.currencyapp.utils.toCountryFlag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterCard(
    fromAmountText: String,
    toAmountText: String,
    fromCurrency: String,
    toCurrency: String,
    onFromAmountChange: (String) -> Unit,
    onFromCurrencyClick: () -> Unit,
    onToCurrencyClick: () -> Unit,
    onToResultChange: (String) -> Unit,

    onSwapClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Currency Converter",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Column(
                modifier = Modifier.padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                InputBlock(
                    title = "From",
                    placeholder = "amount",
                    amount = fromAmountText,
                    onValueChange = onFromAmountChange,
                    currency = fromCurrency,
                    onCurrencyClick = onFromCurrencyClick
                )

                IconButton(
                    onClick = onSwapClick,
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Change the currencies the other way around",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                InputBlock(
                    title = "To",
                    placeholder = "result",
                    amount = toAmountText,
                    onValueChange = onToResultChange,
                    currency = toCurrency,
                    onCurrencyClick = onToCurrencyClick
                )

            }
        }


    }

}

@Composable
fun InputBlock(
    title: String,
    placeholder: String? = null,
    amount: String,
    onValueChange: (String) -> Unit,
    currency: String,
    onCurrencyClick: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = title,
        )
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomTextField(
                value = amount,
                placeholder = placeholder,
                onValueChange = onValueChange,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.6f)
            )

            Card(
                modifier = Modifier.weight(0.4f),
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
}