package com.khyzhniak.currencyapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.utils.toCountryFlag


@Preview
@Composable
fun Test1(){
    RatesBottomSheet(
        listOf("EUR","USD","UAH","RUB","ASG","PND","EUR","USD","UAH","RUB","ASG","PND"), onDismissRequest = {}, onCurrencySelected = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun RatesBottomSheet(
    currencyList: List<String>,
    onCurrencySelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
){
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BottomSheetContent(
            currencyList = currencyList,
            onCurrencySelected = onCurrencySelected,
            onDismissRequest = onDismissRequest
        )
    }
}

@Composable
fun BottomSheetContent(
    currencyList: List<String>,
    onCurrencySelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
){
    var searchQuery by remember{ mutableStateOf("") }

    val filteredCurrencyList = remember(searchQuery,currencyList) {
        currencyList.filter { it.contains(searchQuery, ignoreCase = true)}
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.6f)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Select Currency",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                    onClick = onDismissRequest,
            ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close the Bottom Sheet",
                tint = MaterialTheme.colorScheme.onSurface)
            }
        }


        CustomTextField(
            value = searchQuery,
            onValueChange = {searchQuery = it},
            placeholder = "Search",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredCurrencyList) {
                currencyCode ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    ),
                    onClick = {
                        onCurrencySelected(currencyCode)
                        onDismissRequest()
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currencyCode.toCountryFlag(),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = currencyCode,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = Bold
                        )
                    }

                }
            }
        }
    }
}

