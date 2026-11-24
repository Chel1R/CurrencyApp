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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.khyzhniak.currencyapp.domain.model.ThemeMode
import com.khyzhniak.currencyapp.utils.toCountryFlag

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val selected: Boolean = false,
    val badge: @Composable () -> Unit = {}
)

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    drawerItems: List<DrawerItem>,
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    currentMainCurrency: String,
    onMainCurrencySelected: (String) -> Unit,
    availableCurrencies: List<String> = listOf("USD", "EUR", "GBP", "UAH", "JPY", "CAD", "AUD", "CHF"),
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeaderDrawerSection()

                        drawerItems.forEach { item ->
                            NavigationDrawerItem(
                                label = { Text(text = item.label) },
                                onClick = item.onClick,
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                selected = item.selected,
                                badge = { item.badge() }
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        ThemeDrawerSection(
                            currentThemeMode = currentThemeMode,
                            onThemeModeSelected = onThemeModeSelected
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        CurrencyDrawerSection(
                            currentMainCurrency = currentMainCurrency,
                            onMainCurrencySelected = onMainCurrencySelected,
                            availableCurrencies = availableCurrencies
                        )
                    }

                }
            }
        },
        drawerState = drawerState
    ) {
        content()
    }
}

@Composable
fun HeaderDrawerSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CurrencyExchange,
            contentDescription = "App Logo",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "CurrencyApp",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ThemeDrawerSection(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "App Theme",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ThemeMode.entries.forEach { mode ->
                val label = when (mode) {
                    ThemeMode.LIGHT_THEME -> "Light"
                    ThemeMode.DARK_THEME -> "Dark"
                    ThemeMode.SYSTEM_THEME -> "System"
                }
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = mode == currentThemeMode,
                    onClick = { onThemeModeSelected(mode) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CurrencyDrawerSection(
    currentMainCurrency: String,
    onMainCurrencySelected: (String) -> Unit,
    availableCurrencies: List<String>
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val currencies = remember(availableCurrencies) {
        if (availableCurrencies.isNotEmpty()) availableCurrencies else listOf("USD", "EUR", "GBP", "UAH", "JPY", "CAD", "AUD", "CHF")
    }

    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Main Base Currency",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedCard(
            onClick = { showBottomSheet = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentMainCurrency.toCountryFlag(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = currentMainCurrency,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Main Currency",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showBottomSheet) {
        RatesBottomSheet(
            currencyList = currencies,
            onCurrencySelected = { selectedCurrency ->
                onMainCurrencySelected(selectedCurrency)
                showBottomSheet = false
            },
            onDismissRequest = { showBottomSheet = false }
        )
    }
}

