package com.murilo.petcare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    tertiary = TertiaryGreen,
    background = BackgroundLight,
    error = ErrorRed
)

@Composable
fun PetCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Para simplificar agora, usaremos o LightColorScheme,
    // mas você pode definir um DarkColorScheme depois.
    val colorScheme = if (darkTheme) LightColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}