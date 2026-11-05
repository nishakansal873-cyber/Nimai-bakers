package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =========================================================================
// NIMAI BAKERY CORE COLOR PALETTE: Cream, Pastel Pink & Chocolate-Brown
// =========================================================================

// 1. Chocolate-Brown Tones (Rich artisan cocoa, dark crust, strong typography)
val ChocolateBrown = Color(0xFF3E2723)          // Primary deep chocolate
val ChocolateBrownDark = Color(0xFF26140E)      // Deep roast cocoa
val ChocolateBrownLight = Color(0xFF5D4037)     // Warm cocoa brown
val ChocolateBrownContainer = Color(0xFF4A2C2A) // Rich mocha container
val OnChocolateBrown = Color(0xFFFFF8F0)        // Warm ivory contrast on chocolate

// 2. Cream Tones (Velvety vanilla, warm dough, soft background surfaces)
val CreamBackground = Color(0xFFFFFBF5)         // Pristine artisan cream background
val CreamSurface = Color(0xFFFFF7ED)            // Soft warm cream for cards & panels
val CreamContainer = Color(0xFFFDEFD9)          // Subtle golden cream container
val CreamBorder = Color(0xFFE8D5C4)             // Delicate buttery border outline
val CreamIvory = Color(0xFFFFFDF9)              // Purest cream highlight

// 3. Pastel Pink Tones (Sweet strawberry frosting, blush accents, badges)
val PastelPink = Color(0xFFF48FB1)              // Sweet pastel pink frosting
val PastelPinkSoft = Color(0xFFFCE4EC)          // Gentle blush highlight & chip background
val PastelPinkContainer = Color(0xFFF8BBD0)      // Accent card and badge pink
val PastelPinkDeep = Color(0xFFC2185B)          // Deep berry pink for high-contrast accents

// Complementary Artisan Accents
val BakedCaramel = Color(0xFFD97706)            // Golden caramelized crust
val WarmTextBrown = Color(0xFF2D1810)           // Primary text dark chocolate
val WarmTextMuted = Color(0xFF6B4C42)           // Secondary text warm cocoa

// =========================================================================
// MATERIAL 3 COLOR SCHEMES
// =========================================================================

val BakeryLightColorScheme: ColorScheme = lightColorScheme(
    primary = ChocolateBrown,
    onPrimary = OnChocolateBrown,
    primaryContainer = PastelPinkSoft,
    onPrimaryContainer = ChocolateBrown,
    secondary = PastelPinkDeep,
    onSecondary = Color.White,
    secondaryContainer = PastelPinkContainer,
    onSecondaryContainer = ChocolateBrownDark,
    tertiary = BakedCaramel,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = ChocolateBrownDark,
    background = CreamBackground,
    onBackground = WarmTextBrown,
    surface = CreamSurface,
    onSurface = WarmTextBrown,
    surfaceVariant = CreamContainer,
    onSurfaceVariant = WarmTextMuted,
    outline = CreamBorder,
    outlineVariant = Color(0xFFF0E0D0)
)

val BakeryDarkColorScheme: ColorScheme = darkColorScheme(
    primary = PastelPink,
    onPrimary = ChocolateBrownDark,
    primaryContainer = ChocolateBrownContainer,
    onPrimaryContainer = PastelPinkSoft,
    secondary = PastelPinkContainer,
    onSecondary = ChocolateBrownDark,
    secondaryContainer = ChocolateBrownLight,
    onSecondaryContainer = CreamBackground,
    tertiary = Color(0xFFF59E0B),
    onTertiary = ChocolateBrownDark,
    background = Color(0xFF1E1410),
    onBackground = CreamBackground,
    surface = Color(0xFF281C16),
    onSurface = CreamBackground,
    surfaceVariant = Color(0xFF382720),
    onSurfaceVariant = Color(0xFFD7C2B8),
    outline = Color(0xFF533E35)
)

// =========================================================================
// CUSTOM BAKERY THEME EXTENSION
// =========================================================================

@Immutable
data class BakeryCustomColors(
    val cream: Color = CreamBackground,
    val creamSurface: Color = CreamSurface,
    val pastelPink: Color = PastelPink,
    val pastelPinkSoft: Color = PastelPinkSoft,
    val chocolateBrown: Color = ChocolateBrown,
    val chocolateBrownLight: Color = ChocolateBrownLight,
    val caramel: Color = BakedCaramel
)

val LocalBakeryColors = staticCompositionLocalOf { BakeryCustomColors() }

object BakeryTheme {
    val colors: BakeryCustomColors
        @Composable
        get() = LocalBakeryColors.current
}

// =========================================================================
// APPLICATION THEME COMPOSABLE
// =========================================================================

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Preserve custom artisanal bakery brand identity by keeping dynamicColor false
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BakeryDarkColorScheme else BakeryLightColorScheme
    val customBakeryColors = BakeryCustomColors(
        cream = if (darkTheme) Color(0xFF281C16) else CreamBackground,
        creamSurface = if (darkTheme) Color(0xFF382720) else CreamSurface,
        pastelPink = PastelPink,
        pastelPinkSoft = PastelPinkSoft,
        chocolateBrown = if (darkTheme) Color(0xFFFFF8F0) else ChocolateBrown,
        chocolateBrownLight = ChocolateBrownLight,
        caramel = BakedCaramel
    )

    CompositionLocalProvider(LocalBakeryColors provides customBakeryColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
