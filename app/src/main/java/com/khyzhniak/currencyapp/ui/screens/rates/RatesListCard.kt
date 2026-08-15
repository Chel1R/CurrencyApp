package com.khyzhniak.currencyapp.ui.screens.rates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.utils.toCountryFlag

@Composable
fun RatesListCard(
    currencyList: Map<String,String>,
    mainCurrency: String,
){
    ElevatedCard(
        Modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            currencyList.forEach { (toCurrency, rate) ->
                RateCard(
                    mainCurrency = mainCurrency,
                    toCurrency = toCurrency,
                    rateFrom = "1",
                    rateTo = rate
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateCard(
    mainCurrency: String,
    toCurrency: String,
    rateFrom : String,
    rateTo: String,
){
    Card(
        Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CurrencyPart(
                currency = mainCurrency,
                rate = rateFrom
            )

            Icon(
                contentDescription = "",
                imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                tint = MaterialTheme.colorScheme.primary,
            )
            CurrencyPart(
                currency = toCurrency,
                rate = rateTo
            )
        }
    }
}


@Composable
fun CurrencyPart(
    currency : String,
    rate: String
){
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = currency.toCountryFlag(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = currency,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = rate,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}