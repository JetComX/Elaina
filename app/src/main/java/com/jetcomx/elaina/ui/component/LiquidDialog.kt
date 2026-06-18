package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun LiquidDialog(
    visible: Boolean,
    backdrop: Backdrop,
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    useLiquidButton: Boolean = true,
) {
    if (visible) {
        val isLightTheme = !isSystemInDarkTheme()
        val contentColor = if (isLightTheme) Color.Black else Color.White
        val accentColor = if (isLightTheme) Color(0xFF0088FF) else Color(0xFF0091FF)
        val containerColor = if (isLightTheme) Color(0xFFFAFAFA).copy(alpha = 0.6f)
        else Color(0xFF121212).copy(alpha = 0.4f)

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(40.dp)
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { RoundedRectangle(48.dp) },
                            effects = {
                                colorControls(
                                    brightness = if (isLightTheme) 0.2f else 0f,
                                    saturation = 1.5f
                                )
                                blur(if (isLightTheme) 16.dp.toPx() else 8.dp.toPx())
                                lens(
                                    24.dp.toPx(), 48.dp.toPx(),
                                    chromaticAberration = AppSettings.glassChromaticAberrationEnabled.value,
                                    depthEffect = true
                                )
                            },
                            highlight = if (AppSettings.glassHighlightEnabled.value) {
                                { Highlight.Plain }
                            } else {
                                null
                            },
                            onDrawSurface = { drawRect(containerColor) }
                        )
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(28.dp, 24.dp, 28.dp, 12.dp),
                        style = TextStyle(contentColor, 24.sp, FontWeight.Medium)
                    )
                    Text(
                        text = text,
                        modifier = Modifier
                            .then(
                                if (isLightTheme) Modifier
                                else Modifier.graphicsLayer(blendMode = BlendMode.Plus)
                            )
                            .padding(24.dp, 12.dp, 24.dp, 12.dp),
                        style = TextStyle(contentColor.copy(alpha = 0.68f), 15.sp),
                        maxLines = 5
                    )

                    Row(
                        modifier = Modifier
                            .padding(24.dp, 12.dp, 24.dp, 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (useLiquidButton) {
                            LiquidButton(
                                onClick = onDismiss,
                                backdrop = backdrop,
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text(text = dismissText, color = contentColor, fontSize = 16.sp)
                            }
                            LiquidButton(
                                onClick = onConfirm,
                                backdrop = backdrop,
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text(text = confirmText, color = Color.White, fontSize = 16.sp)
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(containerColor.copy(alpha = 0.2f))
                                    .clickable { onDismiss() }
                                    .height(48.dp)
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = dismissText, style = TextStyle(contentColor, 16.sp))
                            }
                            Row(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(accentColor)
                                    .clickable { onConfirm() }
                                    .height(48.dp)
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = confirmText, style = TextStyle(Color.White, 16.sp))
                            }
                        }
                    }
                }
            }
        }
    }
}