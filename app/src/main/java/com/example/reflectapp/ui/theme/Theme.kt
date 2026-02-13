package com.example.reflectapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Grey99,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    secondary = Teal40,
    onSecondary = Grey99,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal20,
    tertiary = Amber40,
    onTertiary = Grey99,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Grey10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    surfaceVariant = Grey95,
    onSurfaceVariant = Grey20,
    error = Red40,
    onError = Grey99,
    errorContainer = Red90,
    onErrorContainer = Red40
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple30,
    onPrimaryContainer = Purple90,
    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal20,
    onSecondaryContainer = Teal90,
    tertiary = Amber80,
    onTertiary = Grey10,
    tertiaryContainer = Amber40,
    onTertiaryContainer = Amber90,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey90,
    surfaceVariant = Grey20,
    onSurfaceVariant = Grey90,
    error = Red80,
    onError = Red40,
    errorContainer = Red40,
    onErrorContainer = Red90
)

/**
 * The top-level Material 3 theme composable for the Reflect app.
 *
 * Applies dynamic color on Android 12+ devices, falling back to the custom
 * [LightColorScheme] / [DarkColorScheme] on older versions.
 *
 * @param darkTheme Whether to use the dark colour scheme. Defaults to the system setting.
 * @param dynamicColor Whether to use Material You dynamic colour. Only applies on API 31+.
 * @param content The composable content to theme.
 */
@Composable
fun ReflectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
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
        typography = ReflectTypography,
        content = content
    )
}
