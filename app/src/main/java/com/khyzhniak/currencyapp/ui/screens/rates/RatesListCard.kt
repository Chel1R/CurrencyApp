package com.khyzhniak.currencyapp.ui.screens.rates

import android.R
import androidx.collection.intLongMapOf
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.utils.getCurrencyDisplayName
import com.khyzhniak.currencyapp.utils.toCountryFlag

@Composable
fun RatesListCard(
    currencyList: Map<String,String>,
    mainCurrency: String,
){
    ElevatedCard(
        Modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Exchange Rates",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Button for refreshing currencies",
                    )
                }
            }

            currencyList.forEach { (toCurrency, rate) ->
                RateCard(
                    mainCurrency = toCurrency,
                    toCurrency = mainCurrency,
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
    rateTo: String,
){
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CurrencyPart(
                top = mainCurrency,
                bottom = mainCurrency.getCurrencyDisplayName(),

            )

//            Icon(
//                contentDescription = "",
//                imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
//            )
            CurrencyPart(
                top = rateTo,
                bottom = "per $toCurrency",
                alignment = Alignment.End,
                fontWeightTop = FontWeight.Bold
            )
        }
    }
}


@Composable
fun CurrencyPart(
    top : String,
    bottom: String,
    fontWeightTop: FontWeight = FontWeight.Normal,
    alignment: Alignment.Horizontal = Alignment.Start
){

    Column(
        modifier = Modifier,
        horizontalAlignment = alignment
    ) {
        Text(
            text = top,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = fontWeightTop,
        )
        Text(
            text = bottom,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}