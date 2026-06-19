package com.jetcomx.elaina.ui.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp
import com.jetcomx.elaina.ui.utils.InteractiveHighlight
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.shapes.Capsule
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

@Composable
fun LiquidCheckBox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    checkedColor: Color = MiuixTheme.colorScheme.onPrimary,
    uncheckedColor: Color = MiuixTheme.colorScheme.secondary,
    disabledCheckedColor: Color = MiuixTheme.colorScheme.disabledOnPrimary,
    disabledUncheckedColor: Color = MiuixTheme.colorScheme.disabledOnPrimary,
    enabled: Boolean = true,
) {
    val currentOnClickState = rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)

    val transition = updateTransition(state, label = "LiquidCheckboxTransition")

    val checkColorState = transition.animateColor(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "CheckColor",
    ) {
        if (enabled) {
            if (it != ToggleableState.Off) checkedColor else uncheckedColor
        } else {
            if (it != ToggleableState.Off) disabledCheckedColor else disabledUncheckedColor
        }
    }

    val checkAlphaState = transition.animateFloat(
        transitionSpec = {
            if (targetState != ToggleableState.Off) tween(durationMillis = 10, easing = FastOutSlowInEasing)
            else tween(durationMillis = 150, easing = FastOutSlowInEasing)
        },
        label = "CheckAlpha",
    ) { if (it != ToggleableState.Off) 1f else 0f }

    val checkStartTrimState = transition.animateFloat(
        transitionSpec = {
            if (targetState != ToggleableState.Off) tween(durationMillis = 200, easing = FastOutSlowInEasing)
            else keyframes { durationMillis = 300; 0.1f at 300 }
        },
        label = "CheckStartTrim",
    ) { if (it != ToggleableState.Off) 0.186f else 0.1f }

    val checkEndTrimState = transition.animateFloat(
        transitionSpec = {
            if (targetState != ToggleableState.Off) {
                keyframes { durationMillis = 300; 0.85f at 200 using FastOutSlowInEasing; 0.803f at 300 using FastOutSlowInEasing }
            } else {
                keyframes { durationMillis = 300; 0.1f at 300 }
            }
        },
        label = "CheckEndTrim",
    ) { if (it != ToggleableState.Off) 0.803f else 0.1f }

    val crossCenterGravitationState = transition.animateFloat(
        transitionSpec = {
            if (targetState == ToggleableState.Indeterminate) tween(durationMillis = 200, easing = FastOutSlowInEasing)
            else tween(durationMillis = 150, easing = FastOutSlowInEasing)
        },
        label = "CrossCenterGravitation",
    ) { if (it == ToggleableState.Indeterminate) 1f else 0f }

    val capsuleShape = Capsule()
    val checkPath = remember { Path() }
    val animationScope = rememberCoroutineScope()
    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(animationScope = animationScope)
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .requiredSize(26.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { capsuleShape },
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
                    translationX = maxOffset * tanh(initialDerivative * offset.x / maxOffset)
                    translationY = maxOffset * tanh(initialDerivative * offset.y / maxOffset)

                    val maxDragScale = 4f.dp.toPx() / size.height
                    val offsetAngle = atan2(offset.y, offset.x)
                    scaleX = scale +
                            maxDragScale * abs(cos(offsetAngle) * offset.x / size.maxDimension) *
                            (width / height).fastCoerceAtMost(1f)
                    scaleY = scale +
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
                if (enabled) {
                    Modifier
                        .then(if (AppSettings.glassHighlightEnabled.value) interactiveHighlight.modifier else Modifier)
                        .then(interactiveHighlight.gestureModifier)
                } else {
                    Modifier
                }
            )
            .drawWithCache {
                val strokeWidth = size.width * 0.09f
                val centerX = size.width / 2
                val centerY = size.height / 2
                val viewportSize = 23f
                val viewportCenterX = viewportSize / 2
                val viewportCenterY = viewportSize / 2

                val startPoint = Offset(
                    centerX + ((5f - viewportCenterX) / viewportSize * size.width),
                    centerY + ((9.4f - viewportCenterY) / viewportSize * size.height),
                )
                val middlePoint = Offset(
                    centerX + ((10.3f - viewportCenterX) / viewportSize * size.width),
                    centerY + ((14.9f - viewportCenterY) / viewportSize * size.height),
                )
                val endPoint = Offset(
                    centerX + ((17.9f - viewportCenterX) / viewportSize * size.width),
                    centerY + ((5.1f - viewportCenterY) / viewportSize * size.height),
                )

                val cache = CheckmarkCache(startPoint, middlePoint, endPoint, centerX, centerY, strokeWidth)
                val stroke = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    miter = 10.0f,
                )

                onDrawBehind {
                    drawCircle(Color.White.copy(alpha = 0.15f))
                    drawTrimmedCheckmark(
                        color = checkColorState.value,
                        alpha = checkAlphaState.value,
                        trimStart = checkStartTrimState.value,
                        trimEnd = checkEndTrimState.value,
                        crossCenterGravitation = crossCenterGravitationState.value,
                        path = checkPath,
                        cache = cache,
                        stroke = stroke,
                    )
                }
            }
            .clip(capsuleShape)
            .triStateToggleable(
                state = state,
                onClick = {
                    currentOnClickState.value?.invoke()
                    currentHapticFeedback.performHapticFeedback(
                        when (state) {
                            ToggleableState.Off -> HapticFeedbackType.ToggleOn
                            ToggleableState.On -> HapticFeedbackType.ToggleOff
                            ToggleableState.Indeterminate -> HapticFeedbackType.SegmentTick
                        }
                    )
                },
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
    )
}

private data class CheckmarkCache(
    val startPoint: Offset,
    val middlePoint: Offset,
    val endPoint: Offset,
    val centerX: Float,
    val centerY: Float,
    val strokeWidth: Float,
)

private fun DrawScope.drawTrimmedCheckmark(
    color: Color,
    alpha: Float = 1f,
    trimStart: Float,
    trimEnd: Float,
    crossCenterGravitation: Float,
    path: Path,
    cache: CheckmarkCache,
    stroke: Stroke,
) {
    path.rewind()
    val gravitatedStart = Offset(cache.startPoint.x, lerp(cache.startPoint.y, cache.centerY, crossCenterGravitation))
    val gravitatedMiddle = Offset(lerp(cache.middlePoint.x, cache.centerX, crossCenterGravitation), lerp(cache.middlePoint.y, cache.centerY, crossCenterGravitation))
    val gravitatedEnd = Offset(cache.endPoint.x, lerp(cache.endPoint.y, cache.centerY, crossCenterGravitation))

    val firstSegmentLength = (gravitatedMiddle - gravitatedStart).getDistance()
    val secondSegmentLength = (gravitatedEnd - gravitatedMiddle).getDistance()
    val totalLength = firstSegmentLength + secondSegmentLength
    val startDistance = totalLength * trimStart
    val endDistance = totalLength * trimEnd

    if (startDistance < firstSegmentLength && endDistance > 0) {
        val segStartRatio = (startDistance / firstSegmentLength).coerceIn(0f, 1f)
        val segEndRatio = (endDistance / firstSegmentLength).coerceIn(0f, 1f)
        val startX = gravitatedStart.x + (gravitatedMiddle.x - gravitatedStart.x) * segStartRatio
        val startY = gravitatedStart.y + (gravitatedMiddle.y - gravitatedStart.y) * segStartRatio
        val endX = gravitatedStart.x + (gravitatedMiddle.x - gravitatedStart.x) * segEndRatio
        val endY = gravitatedStart.y + (gravitatedMiddle.y - gravitatedStart.y) * segEndRatio
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
    }

    if (endDistance > firstSegmentLength) {
        val segStartRatio = ((startDistance - firstSegmentLength) / secondSegmentLength).coerceIn(0f, 1f)
        val segEndRatio = ((endDistance - firstSegmentLength) / secondSegmentLength).coerceIn(0f, 1f)
        val startX = gravitatedMiddle.x + (gravitatedEnd.x - gravitatedMiddle.x) * segStartRatio
        val startY = gravitatedMiddle.y + (gravitatedEnd.y - gravitatedMiddle.y) * segStartRatio
        val endX = gravitatedMiddle.x + (gravitatedEnd.x - gravitatedMiddle.x) * segEndRatio
        val endY = gravitatedMiddle.y + (gravitatedEnd.y - gravitatedMiddle.y) * segEndRatio
        if (startDistance < firstSegmentLength) {
            path.lineTo(endX, endY)
        } else {
            path.moveTo(startX, startY)
            path.lineTo(endX, endY)
        }
    }

    drawPath(path = path, color = color, alpha = alpha, style = stroke)
}