package com.khyzhniak.currencyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.khyzhniak.currencyapp.ui.navigation.AppNavigation
import com.khyzhniak.currencyapp.ui.theme.CurrencyAppTheme
import com.khyzhniak.currencyapp.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel : SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()

            CurrencyAppTheme(themeMode) {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
