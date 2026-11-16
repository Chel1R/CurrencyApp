package com.khyzhniak.currencyapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.khyzhniak.currencyapp.domain.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    surface = DarkCardColor,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceVariant = DarkSurfaceVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = TextSelected,
    secondary = PurpleGrey40,
    onSurface = LightOnSurface,
    background = LightBackground,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceVariant = LightSurfaceVariant,
    surface = LightCardColor,
)

@Composable
fun CurrencyAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM_THEME,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK_THEME -> true
        ThemeMode.LIGHT_THEME -> false
        ThemeMode.SYSTEM_THEME -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = AppShapes
    )
}
