package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeSetting(
    val id: String,
    val title: String,
    val subtitle: String,
    val previewPrimary: Color,
    val previewSecondary: Color
) {
    INDIAN_TRADITIONAL(
        id = "INDIAN_TRADITIONAL",
        title = "Indian Traditional",
        subtitle = "Royal Marigold Saffron & Peacock Blue",
        previewPrimary = IndianTradPrimaryLight,
        previewSecondary = IndianTradSecondaryLight
    ),
    SAKURA(
        id = "SAKURA",
        title = "Sakura",
        subtitle = "Soft Cherry Blossom Pink & Rose",
        previewPrimary = SakuraPrimaryLight,
        previewSecondary = SakuraSecondaryLight
    ),
    OCEAN(
        id = "OCEAN",
        title = "Ocean",
        subtitle = "Deep Marine Azure & Aquamarine",
        previewPrimary = OceanPrimaryLight,
        previewSecondary = OceanSecondaryLight
    ),
    SKY(
        id = "SKY",
        title = "Sky",
        subtitle = "Airy Blue & Cerulean Horizon",
        previewPrimary = SkyPrimaryLight,
        previewSecondary = SkySecondaryLight
    ),
    FIRE(
        id = "FIRE",
        title = "Fire",
        subtitle = "Blazing Ember & Fiery Crimson",
        previewPrimary = FirePrimaryLight,
        previewSecondary = FireSecondaryLight
    ),
    WIND(
        id = "WIND",
        title = "Wind",
        subtitle = "Fresh Eucalyptus Sage & Mint",
        previewPrimary = WindPrimaryLight,
        previewSecondary = WindSecondaryLight
    );

    companion object {
        fun fromId(id: String): AppThemeSetting {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: INDIAN_TRADITIONAL
        }
    }
}

// 1. Sakura
private val SakuraLight = lightColorScheme(
    primary = SakuraPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE4E6),
    onPrimaryContainer = Color(0xFF881337),
    secondary = SakuraSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF1F2),
    onSecondaryContainer = Color(0xFF9F1239),
    tertiary = SakuraTertiary,
    background = SakuraBgLight,
    surface = SakuraSurfaceLight,
    surfaceVariant = Color(0xFFFCE7F3),
    onBackground = Color(0xFF1E1014),
    onSurface = Color(0xFF1E1014),
    onSurfaceVariant = Color(0xFF6B21A8)
)
private val SakuraDark = darkColorScheme(
    primary = SakuraPrimaryDark,
    onPrimary = Color(0xFF4C0519),
    primaryContainer = Color(0xFF881337),
    onPrimaryContainer = Color(0xFFFFDDE4),
    secondary = SakuraSecondaryDark,
    onSecondary = Color(0xFF4C0519),
    background = SakuraBgDark,
    surface = SakuraSurfaceDark,
    surfaceVariant = Color(0xFF3F1D2C),
    onBackground = Color(0xFFFFF1F4),
    onSurface = Color(0xFFFFF1F4)
)

// 2. Ocean
private val OceanLight = lightColorScheme(
    primary = OceanPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = OceanSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),
    tertiary = OceanTertiary,
    background = OceanBgLight,
    surface = OceanSurfaceLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)
private val OceanDark = darkColorScheme(
    primary = OceanPrimaryDark,
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = OceanSecondaryDark,
    onSecondary = Color(0xFF042F2E),
    background = OceanBgDark,
    surface = OceanSurfaceDark,
    surfaceVariant = Color(0xFF1E3A5F),
    onBackground = Color(0xFFF0F9FF),
    onSurface = Color(0xFFF0F9FF)
)

// 3. Sky
private val SkyLight = lightColorScheme(
    primary = SkyPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = SkySecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF155E75),
    tertiary = SkyTertiary,
    background = SkyBgLight,
    surface = SkySurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)
private val SkyDark = darkColorScheme(
    primary = SkyPrimaryDark,
    onPrimary = Color(0xFF172554),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = SkySecondaryDark,
    onSecondary = Color(0xFF083344),
    background = SkyBgDark,
    surface = SkySurfaceDark,
    surfaceVariant = Color(0xFF283655),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC)
)

// 4. Fire
private val FireLight = lightColorScheme(
    primary = FirePrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEE2E2),
    onPrimaryContainer = Color(0xFF7F1D1D),
    secondary = FireSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF7C2D12),
    tertiary = FireTertiary,
    background = FireBgLight,
    surface = FireSurfaceLight,
    surfaceVariant = Color(0xFFFED7AA),
    onBackground = Color(0xFF1F0D07),
    onSurface = Color(0xFF1F0D07)
)
private val FireDark = darkColorScheme(
    primary = FirePrimaryDark,
    onPrimary = Color(0xFF450A0A),
    primaryContainer = Color(0xFF991B1B),
    onPrimaryContainer = Color(0xFFFEE2E2),
    secondary = FireSecondaryDark,
    onSecondary = Color(0xFF431407),
    background = FireBgDark,
    surface = FireSurfaceDark,
    surfaceVariant = Color(0xFF4D2415),
    onBackground = Color(0xFFFFF7ED),
    onSurface = Color(0xFFFFF7ED)
)

// 5. Wind
private val WindLight = lightColorScheme(
    primary = WindPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = WindSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF134E4A),
    tertiary = WindTertiary,
    background = WindBgLight,
    surface = WindSurfaceLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onBackground = Color(0xFF0C1E17),
    onSurface = Color(0xFF0C1E17)
)
private val WindDark = darkColorScheme(
    primary = WindPrimaryDark,
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = WindSecondaryDark,
    onSecondary = Color(0xFF042F2E),
    background = WindBgDark,
    surface = WindSurfaceDark,
    surfaceVariant = Color(0xFF1E4636),
    onBackground = Color(0xFFF0FDF4),
    onSurface = Color(0xFFF0FDF4)
)

// 6. Indian Traditional
private val IndianTradLight = lightColorScheme(
    primary = IndianTradPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFFE65100),
    secondary = IndianTradSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8EAF6),
    onSecondaryContainer = Color(0xFF1A237E),
    tertiary = IndianTradTertiary,
    background = IndianTradBgLight,
    surface = IndianTradSurfaceLight,
    surfaceVariant = Color(0xFFFFF3E0),
    onBackground = Color(0xFF1B1307),
    onSurface = Color(0xFF1B1307)
)
private val IndianTradDark = darkColorScheme(
    primary = IndianTradPrimaryDark,
    onPrimary = Color(0xFF3E1F00),
    primaryContainer = Color(0xFFE65100),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = IndianTradSecondaryDark,
    onSecondary = Color.White,
    background = IndianTradBgDark,
    surface = IndianTradSurfaceDark,
    surfaceVariant = Color(0xFF3E2D18),
    onBackground = Color(0xFFFFFBEB),
    onSurface = Color(0xFFFFFBEB)
)

@Composable
fun MyApplicationTheme(
    themeSetting: AppThemeSetting = AppThemeSetting.INDIAN_TRADITIONAL,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeSetting) {
        AppThemeSetting.SAKURA -> if (darkTheme) SakuraDark else SakuraLight
        AppThemeSetting.OCEAN -> if (darkTheme) OceanDark else OceanLight
        AppThemeSetting.SKY -> if (darkTheme) SkyDark else SkyLight
        AppThemeSetting.FIRE -> if (darkTheme) FireDark else FireLight
        AppThemeSetting.WIND -> if (darkTheme) WindDark else WindLight
        AppThemeSetting.INDIAN_TRADITIONAL -> if (darkTheme) IndianTradDark else IndianTradLight
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
