package com.jetcomx.elaina.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

@Immutable
class LiquidTabRowColors(
    val backgroundColor: Color,
    val contentColor: Color,
    val selectedBackgroundColor: Color,
    val selectedContentColor: Color,
) {
    @Stable
    fun background(selected: Boolean): Color = if (selected) selectedBackgroundColor else backgroundColor

    @Stable
    fun content(selected: Boolean): Color = if (selected) selectedContentColor else contentColor

    companion object {
        @Composable
        fun defaults(
            backgroundColor: Color = MiuixTheme.colorScheme.surfaceContainer,
            contentColor: Color = MiuixTheme.colorScheme.onSurfaceVariantActions,
            selectedBackgroundColor: Color = MiuixTheme.colorScheme.primary,
            selectedContentColor: Color = MiuixTheme.colorScheme.onPrimary,
        ): LiquidTabRowColors = remember(
            backgroundColor, contentColor, selectedBackgroundColor, selectedContentColor
        ) {
            LiquidTabRowColors(backgroundColor, contentColor, selectedBackgroundColor, selectedContentColor)
        }
    }
}

private data class LiquidTabRowConfig(
    val tabWidth: Dp,
    val shape: Shape,
    val listState: androidx.compose.foundation.lazy.LazyListState,
)

@Composable
private fun rememberLiquidTabRowConfig(
    tabs: List<String>,
    minWidth: Dp,
    maxWidth: Dp,
    cornerRadius: Dp,
    spacing: Dp,
    lazyRowAvailableWidth: Dp,
): LiquidTabRowConfig {
    val listState = rememberLazyListState()
    val tabWidth = remember(tabs.size, minWidth, maxWidth, lazyRowAvailableWidth, spacing) {
        calculateTabWidth(tabs.size, minWidth, maxWidth, spacing, lazyRowAvailableWidth)
    }
    val shape = RoundedCornerShape(cornerRadius)
    return LiquidTabRowConfig(tabWidth, shape, listState)
}

private fun calculateTabWidth(
    tabCount: Int,
    minWidth: Dp,
    maxWidth: Dp,
    spacing: Dp,
    availableWidth: Dp,
): Dp {
    if (tabCount == 0) return minWidth
    val totalSpacing = if (tabCount > 1) (tabCount - 1) * spacing else 0.dp
    val contentWidth = availableWidth - totalSpacing
    if (contentWidth <= 0.dp) return minWidth
    val idealWidth = contentWidth / tabCount
    return when {
        idealWidth < minWidth -> minWidth
        idealWidth > maxWidth -> {
            val totalMaxWidth = maxWidth * tabCount + totalSpacing
            if (totalMaxWidth < availableWidth) idealWidth else maxWidth
        }
        else -> idealWidth
    }
}

@Composable
fun LiquidTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    colors: LiquidTabRowColors = LiquidTabRowColors.defaults(),
    minWidth: Dp = 76.dp,
    maxWidth: Dp = 98.dp,
    height: Dp = 42.dp,
    cornerRadius: Dp = 12.dp,
    itemSpacing: Dp = 9.dp,
    contentAlignment: Alignment = Alignment.Center,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val shape = RoundedCornerShape(cornerRadius)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(height)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(4f.dp.toPx())
                    lens(12f.dp.toPx(), 12f.dp.toPx(), chromaticAberration = AppSettings.glassChromaticAberrationEnabled.value)
                },
                onDrawSurface = { drawRect(colors.background(false)) }
            ),
    ) {
        val config = rememberLiquidTabRowConfig(tabs, minWidth, maxWidth, cornerRadius, itemSpacing, this.maxWidth)
        val density = LocalDensity.current
        val tabWidthPx = with(density) { config.tabWidth.toPx() }
        val spacingPx = with(density) { itemSpacing.toPx() }
        val indicatorOffset = remember { Animatable(0f) }
        val availableWidth = this.maxWidth

        LaunchedEffect(selectedTabIndex, tabWidthPx, spacingPx) {
            val target = selectedTabIndex * (tabWidthPx + spacingPx)
            indicatorOffset.animateTo(target, tween(200, easing = LinearEasing))
        }

        LaunchedEffect(selectedTabIndex, availableWidth) {
            val centerOffset = (availableWidth - config.tabWidth) / 2
            val offsetPx = with(density) { -centerOffset.toPx() }.roundToInt()
            config.listState.animateScrollToItem(selectedTabIndex, offsetPx)
        }

        val scrollOffset by remember {
            derivedStateOf {
                val state = config.listState
                val firstIndex = state.firstVisibleItemIndex
                val firstOffset = state.firstVisibleItemScrollOffset
                firstIndex * (tabWidthPx + spacingPx) + firstOffset
            }
        }

        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .offset { IntOffset((indicatorOffset.value - scrollOffset).roundToInt(), 0) }
                    .width(config.tabWidth)
                    .fillMaxHeight()
                    .clip(config.shape)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { config.shape },
                        effects = {
                            vibrancy()
                            blur(6f.dp.toPx())
                            lens(16f.dp.toPx(), 16f.dp.toPx(), chromaticAberration = AppSettings.glassChromaticAberrationEnabled.value)
                        },
                        onDrawSurface = { drawRect(colors.background(true)) }
                    ),
            )
            LazyRow(
                state = config.listState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                overscrollEffect = null,
            ) {
                itemsIndexed(tabs) { index, tabText ->
                    LiquidTabItem(
                        text = tabText,
                        isSelected = selectedTabIndex == index,
                        onClick = { currentOnTabSelected.invoke(index) },
                        shape = config.shape,
                        width = config.tabWidth,
                        color = colors.content(selectedTabIndex == index),
                        contentAlignment = contentAlignment,
                        interactionSource = interactionSource,
                        indication = indication,
                    )
                }
            }
        }
    }
}

@Composable
private fun LiquidTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    shape: Shape,
    width: Dp,
    color: Color = Color.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    Surface(
        shape = shape,
        onClick = { currentOnClick() },
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .semantics { role = Role.Tab },
        interactionSource = interactionSource,
        indication = indication,
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            contentAlignment = contentAlignment,
        ) {
            Text(
                text = text,
                color = color,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = MiuixTheme.textStyles.body1.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
