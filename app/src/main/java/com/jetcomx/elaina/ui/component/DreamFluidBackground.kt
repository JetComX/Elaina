package com.jetcomx.elaina.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.sqrt
import kotlin.random.Random

private val Palette = listOf(
    Color(0xFFD32F2F), Color(0xFFE53935), Color(0xFFE91E63), Color(0xFFF06292),
    Color(0xFF9C27B0), Color(0xFFAB47BC), Color(0xFF7B1FA2),
    Color(0xFF673AB7), Color(0xFF5C6BC0), Color(0xFF3F51B5), Color(0xFF1976D2),
    Color(0xFF0288D1), Color(0xFF0097A7), Color(0xFF009688), Color(0xFF00897B),
    Color(0xFF43A047), Color(0xFF66BB6A), Color(0xFF8BC34A),
    Color(0xFFFDD835), Color(0xFFFFB300), Color(0xFFFF9800), Color(0xFFF57C00),
    Color(0xFFE65100), Color(0xFFBF360C),
    Color(0xFF795548), Color(0xFF8D6E63), Color(0xFF607D8B), Color(0xFF90A4AE),
    Color(0xFFF8BBD0), Color(0xFFF48FB1), Color(0xFFCE93D8), Color(0xFFB39DDB),
    Color(0xFF90CAF9), Color(0xFF81D4FA), Color(0xFF80DEEA), Color(0xFF80CBC4),
    Color(0xFFFFAB91), Color(0xFFFFCC80), Color(0xFFFFF176),
)

private val BaseColor = Color(0xFF08081A)

@Composable
fun DreamFluidBackground(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition()
    val paletteSize = Palette.size

    
    val anchors = remember {
        val rng = Random(77)
        buildList {
            
            repeat(6) {
                add(
                    Anchor(
                        xFrac = 0.1f + rng.nextFloat() * 0.8f,
                        yFrac = 0.1f + rng.nextFloat() * 0.8f,
                        radFrac = 0.7f + rng.nextFloat() * 0.5f,
                        palIdx = rng.nextInt(paletteSize),
                        alphaLo = 0.02f + rng.nextFloat() * 0.03f,
                        alphaHi = 0.05f + rng.nextFloat() * 0.06f,
                        cycleMs = 7000 + rng.nextInt(5000),
                    )
                )
            }
            
            repeat(9) {
                add(
                    Anchor(
                        xFrac = 0.08f + rng.nextFloat() * 0.84f,
                        yFrac = 0.08f + rng.nextFloat() * 0.84f,
                        radFrac = 0.35f + rng.nextFloat() * 0.35f,
                        palIdx = rng.nextInt(paletteSize),
                        alphaLo = 0.03f + rng.nextFloat() * 0.04f,
                        alphaHi = 0.07f + rng.nextFloat() * 0.08f,
                        cycleMs = 6000 + rng.nextInt(4000),
                    )
                )
            }
            
            repeat(7) {
                add(
                    Anchor(
                        xFrac = 0.05f + rng.nextFloat() * 0.9f,
                        yFrac = 0.05f + rng.nextFloat() * 0.9f,
                        radFrac = 0.18f + rng.nextFloat() * 0.2f,
                        palIdx = rng.nextInt(paletteSize),
                        alphaLo = 0.05f + rng.nextFloat() * 0.05f,
                        alphaHi = 0.10f + rng.nextFloat() * 0.10f,
                        cycleMs = 5000 + rng.nextInt(3000),
                    )
                )
            }
        }
    }

    
    val alphas = anchors.mapIndexed { i, a ->
        val v by infinite.animateFloat(
            initialValue = a.alphaLo,
            targetValue = a.alphaHi,
            animationSpec = infiniteRepeatable(tween(a.cycleMs, easing = LinearEasing), RepeatMode.Reverse),
            label = "a$i",
        )
        v
    }

    
    val hueOffsets = anchors.mapIndexed { i, _ ->
        val h by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                tween(12000 + i * 600, easing = LinearEasing),
                RepeatMode.Restart,
            ),
            label = "h$i",
        )
        h
    }

    
    val ambient by infinite.animateFloat(
        0.88f, 1f,
        infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Reverse),
        label = "ambient",
    )

    Box(modifier = modifier.fillMaxSize().background(BaseColor)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas
            val maxR = sqrt((w * w + h * h).toDouble()).toFloat()
            val cx = w / 2f
            val cy = h / 2f

            
            val sorted = anchors.indices.sortedByDescending { anchors[it].radFrac }

            for (idx in sorted) {
                val a = anchors[idx]
                val alpha = (alphas[idx] * ambient).coerceIn(0f, 0.2f)
                if (alpha < 0.002f) continue

                val color = Palette[(a.palIdx + (hueOffsets[idx] / 20f).toInt()) % paletteSize]
                val pos = Offset(w * a.xFrac, h * a.yFrac)
                val r = maxR * a.radFrac

                
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to color.copy(alpha = alpha),
                        0.15f to color.copy(alpha = alpha * 0.70f),
                        0.35f to color.copy(alpha = alpha * 0.35f),
                        0.6f to color.copy(alpha = alpha * 0.08f),
                        1f to Color.Transparent,
                        center = pos,
                        radius = r,
                    ),
                    radius = r,
                    center = pos,
                )
            }

            
            drawCircle(
                brush = Brush.radialGradient(
                    0.5f to Color.Transparent,
                    0.82f to BaseColor.copy(alpha = 0.30f),
                    1f to BaseColor.copy(alpha = 0.60f),
                    center = Offset(cx, cy),
                    radius = maxR * 1.1f,
                ),
                radius = maxR * 1.1f,
                center = Offset(cx, cy),
            )
        }
    }
}

private data class Anchor(
    val xFrac: Float,
    val yFrac: Float,
    val radFrac: Float,
    val palIdx: Int,
    val alphaLo: Float,
    val alphaHi: Float,
    val cycleMs: Int,
)
