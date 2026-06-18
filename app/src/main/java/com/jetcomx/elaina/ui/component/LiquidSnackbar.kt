package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp
import com.jetcomx.elaina.ui.utils.InteractiveHighlight
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SnackbarData
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.SearchCleanup
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

@Composable
fun LiquidSnackbar(
    data: SnackbarData,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentColor: Color = MiuixTheme.colorScheme.onSurface,
    insideMargin: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
) {
    val shape = RoundedCornerShape(cornerRadius)
    val visuals = data.visuals
    val scope = rememberCoroutineScope()
    val animationScope = rememberCoroutineScope()
    val primary = MiuixTheme.colorScheme.primary

    var frameNanos by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameNanos = it }
        }
    }

    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(
            animationScope = animationScope
        )
    }

    Box(
        modifier = modifier
            .padding(insideMargin)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    standardGlassEffects()
                },
                onDrawSurface = {
                    @Suppress("UNUSED_EXPRESSION")
                    frameNanos
                },
            )
            .background(
                Color.White.copy(alpha = 0.15f),
                shape = shape
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 44.dp)
                .padding(insideMargin)
        ) {
            Text(
                text = visuals.message,
                color = primary,
                style = MiuixTheme.textStyles.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (!visuals.actionLabel.isNullOrEmpty()) {
                val onAction by rememberUpdatedState(data::performAction)
                TextButton(
                    text = visuals.actionLabel!!,
                    onClick = { scope.launch { onAction() } },
                    colors = ButtonDefaults.textButtonColors(
                        color = Color.Transparent,
                        disabledColor = Color.Transparent,
                        textColor = contentColor,
                        disabledTextColor = contentColor,
                    ),
                    insideMargin = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            if (visuals.withDismissAction) {
                val onDismiss by rememberUpdatedState(data::dismiss)
                Icon(
                    imageVector = MiuixIcons.Basic.SearchCleanup,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                        .clip(Capsule())
                        .clickable { scope.launch { onDismiss() } }
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { RoundedCornerShape(50) },
                            effects = {
                                standardGlassEffects()
                            },
                            layerBlock = {
                                val width = size.width
                                val height = size.height

                                val progress = interactiveHighlight.pressProgress
                                val scale = lerp(1f, 1f + 4f.dp.toPx() / size.height, progress)

                                val maxOffset = size.minDimension
                                val initialDerivative = 0.05f
                                val offset = interactiveHighlight.offset
                                translationX =
                                    maxOffset * tanh(initialDerivative * offset.x / maxOffset)
                                translationY =
                                    maxOffset * tanh(initialDerivative * offset.y / maxOffset)

                                val maxDragScale = 4f.dp.toPx() / size.height
                                val offsetAngle = atan2(offset.y, offset.x)
                                scaleX =
                                    scale +
                                            maxDragScale * abs(cos(offsetAngle) * offset.x / size.maxDimension) *
                                            (width / height).fastCoerceAtMost(1f)
                                scaleY =
                                    scale +
                                            maxDragScale * abs(sin(offsetAngle) * offset.y / size.maxDimension) *
                                            (height / width).fastCoerceAtMost(1f)
                            },
                            onDrawSurface = {
                                if (Color.Unspecified.isSpecified) {
                                    drawRect(Color.Unspecified, blendMode = BlendMode.Hue)
                                    drawRect(Color.Unspecified.copy(alpha = 0.75f))
                                }
                                if (Color.Unspecified.isSpecified) {
                                    drawRect(Color.Unspecified)
                                }
                            }
                        )
                        .then(
                            Modifier
                                .then(interactiveHighlight.modifier)
                                .then(interactiveHighlight.gestureModifier)
                        )
                )
            }
        }
    }
}