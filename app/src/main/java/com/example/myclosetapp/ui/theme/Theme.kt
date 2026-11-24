package com.example.myclosetapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrown,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryContainerBeige,
    onPrimaryContainer = OnPrimaryContainerBrown,
    secondary = SecondaryTerracotta,
    onSecondary = OnPrimaryWhite,
    secondaryContainer = SecondaryContainerPink,
    onSecondaryContainer = OnSecondaryContainerDark,
    background = BackgroundOffWhite,
    onBackground = OnBackgroundDarkBrown,
    surface = SurfaceWarmWhite,
    onSurface = OnSurfaceDarkBrown,
    surfaceVariant = SurfaceVariantWarm,
    onSurfaceVariant = OnSurfaceVariantMuted,
    outline = OutlineWarm
)

val ClosetShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyClosetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = ClosetShapes,
        content = content
    )
}
