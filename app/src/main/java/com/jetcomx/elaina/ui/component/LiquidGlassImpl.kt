package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun LiquidGlassCardImpl(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier,
    enabled: Boolean,
    containerColor: Color,
    shape: Shape,
    content: @Composable ColumnScope.() -> Unit
) {
    LiquidCard(
        onClick = onClick,
        backdrop = backdrop,
        modifier = modifier,
        enabled = enabled,
        tint = containerColor,
        shape = shape,
        content = content
    )
}

@Composable
fun LiquidGlassButtonImpl(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier,
    containerColor: Color,
    content: @Composable RowScope.() -> Unit
) {
    LiquidButton(
        onClick = onClick,
        backdrop = backdrop,
        modifier = modifier,
        containerColor = containerColor,
        content = content
    )
}

@Composable
fun LiquidGlassSnackbarHostImpl(
    hostState: SnackbarHostState,
    backdrop: Backdrop,
    modifier: Modifier,
    bottomPadding: Dp = 72.dp,
) {
    SnackbarHost(
        state = hostState,
        modifier = modifier.padding(bottom = bottomPadding)
    ) { data ->
        LiquidSnackbar(
            data = data,
            backdrop = backdrop,
            contentColor = MiuixTheme.colorScheme.onSurface
        )
    }
}
