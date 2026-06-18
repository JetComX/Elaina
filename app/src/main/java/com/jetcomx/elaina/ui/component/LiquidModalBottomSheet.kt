package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp
import com.jetcomx.elaina.ui.utils.InteractiveHighlight
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidModalBottomSheet(
    show: Boolean,
    backdrop: Backdrop,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    enableDismiss: Boolean = true,
    content: @Composable () -> Unit
) {

    val animationScope = rememberCoroutineScope()

    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(
            animationScope = animationScope
        )
    }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { enableDismiss }
            ),
            shape = shape,
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            dragHandle = null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { shape },
                        effects = {
                            vibrancy()
                            blur(2.5f.dp.toPx())
                            lens(12f.dp.toPx(), 24f.dp.toPx(), chromaticAberration = AppSettings.glassChromaticAberrationEnabled.value)
                        }
                    )
                    .background(Color.White.copy(alpha = 0.05f), shape)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = 0.5f))
                                .drawBackdrop(
                                    backdrop = backdrop,
                                    shape = { Capsule() },
                                    effects = {
                                        standardGlassEffects()
                                    },
                                    layerBlock = run {
                                        {
                                            val width = size.width
                                            val height = size.height

                                            val progress = interactiveHighlight.pressProgress
                                            val scale = lerp(1f, 1f + 4f.dp.toPx() / size.height, progress)

                                            val maxOffset = size.minDimension
                                            val initialDerivative = 0.05f
                                            val offset = interactiveHighlight.offset
                                            translationX = maxOffset * tanh(initialDerivative * offset.x / maxOffset)
                                            translationY = maxOffset * tanh(initialDerivative * offset.y / maxOffset)

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
                                        }
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
                                    .then(if (AppSettings.glassHighlightEnabled.value) interactiveHighlight.modifier else Modifier)
                                    .then(interactiveHighlight.gestureModifier)
                                )
                        )
                    }
                    content()
                }
            }
        }
    }
}