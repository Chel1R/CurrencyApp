package com.khyzhniak.currencyapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropDown(
    selectedCurrency : String,
    currencyList : List<String>,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded = it},
        modifier = modifier
    ) {
        CustomTextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
            trailingIcon =  {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )


        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            currencyList.forEach {
                currencyCode ->
                DropdownMenuItem(
                    text = { Text(text = currencyCode) },
                    onClick =  {
                        onCurrencySelected(currencyCode)
                        expanded = false
                    }
                )
            }
        }
    }
}