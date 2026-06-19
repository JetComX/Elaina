package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
fun LiquidWindowDialog(
    show: Boolean,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = MiuixTheme.colorScheme.onBackground,
    summary: String? = null,
    summaryColor: Color = MiuixTheme.colorScheme.onSurfaceSecondary,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = DpSize(12.dp, 12.dp),
    insideMargin: PaddingValues = PaddingValues(24.dp),
    content: @Composable () -> Unit,
) {
    WindowDialog(
        show = show,
        modifier = modifier,
        backgroundColor = Color.Transparent,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        outsideMargin = outsideMargin,
        defaultWindowInsetsPadding = true,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(24.dp) },
                    effects = {
                        standardGlassEffects()
                    }
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(insideMargin)
        ) {
            Column {
                title?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = titleColor,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                }
                summary?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = summaryColor,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                }
                content()
            }
        }
    }
}
