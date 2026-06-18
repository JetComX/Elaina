package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.theme.MiuixTheme

val LocalIsLiquidGlass = staticCompositionLocalOf { false }
val LocalLiquidGlassBackdrop = staticCompositionLocalOf<Backdrop?> { null }
val LocalLiquidGlassOnSurface = staticCompositionLocalOf { Color.Unspecified }
val LocalLiquidGlassSurface = staticCompositionLocalOf { Color.Unspecified }
val LocalBackgroundBackdrop = staticCompositionLocalOf<Backdrop?> { null }

@Composable
fun GlassCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(24.dp),
    containerColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit
) {
    val isGlass = LocalIsLiquidGlass.current
    val backdrop = LocalLiquidGlassBackdrop.current

    if (isGlass && backdrop != null) {
        LiquidGlassCardImpl(
            onClick = onClick,
            backdrop = backdrop,
            modifier = modifier,
            enabled = enabled,
            containerColor = containerColor,
            shape = shape,
            content = content
        )
    } else {
        val color = if (containerColor.isSpecified) containerColor
                    else MiuixTheme.colorScheme.surfaceContainerHighest
        top.yukonga.miuix.kmp.basic.Card(
            modifier = modifier,
            colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = color),
            onClick = onClick,
            content = content
        )
    }
}

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    content: @Composable RowScope.() -> Unit
) {
    val isGlass = LocalIsLiquidGlass.current
    val backdrop = LocalLiquidGlassBackdrop.current

    if (isGlass && backdrop != null) {
        LiquidGlassButtonImpl(
            onClick = onClick,
            backdrop = backdrop,
            modifier = modifier,
            containerColor = containerColor,
            content = content
        )
    } else {
        val colors = if (containerColor.isSpecified) {
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = if (contentColor.isSpecified) contentColor else Color.White
            )
        } else {
            androidx.compose.material3.ButtonDefaults.buttonColors()
        }
        androidx.compose.material3.Button(
            onClick = onClick,
            modifier = modifier,
            colors = colors,
            content = content
        )
    }
}

@Composable
fun GlassSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val isGlass = LocalIsLiquidGlass.current
    val backdrop = LocalLiquidGlassBackdrop.current

    if (isGlass && backdrop != null) {
        LiquidGlassSnackbarHostImpl(
            hostState = hostState,
            backdrop = backdrop,
            modifier = modifier
        )
    } else {
        top.yukonga.miuix.kmp.basic.SnackbarHost(state = hostState, modifier = modifier)
    }
}
