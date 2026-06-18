package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
fun ShaderWarmUp() {
    val warmBackdrop = rememberCanvasBackdrop { drawRect(Color.White) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.003f)
            .drawBackdrop(
                backdrop = warmBackdrop,
                shape = { RoundedCornerShape(24.dp) },
                effects = {
                    vibrancy()
                    blur(2f.dp.toPx())
                    lens(12f.dp.toPx(), 24f.dp.toPx())
                }
            )
    )
}
