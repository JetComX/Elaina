package com.jetcomx.elaina.screen.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetcomx.elaina.R
import com.jetcomx.elaina.navigation.LocalNavigator
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LiquidSlider
import com.jetcomx.elaina.ui.component.LiquidSwitchPreference
import com.jetcomx.elaina.ui.component.LiquidWindowDialog
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SwitchDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Theme
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun LiquidGlassCustomizeScreen() {
    val navigator = LocalNavigator.current
    BackHandler { navigator.pop() }

    val vibrancyEnabled by AppSettings.glassVibrancyEnabled.collectAsState()
    val blurRadius by AppSettings.glassBlurRadius.collectAsState()
    val lensDilation by AppSettings.glassLensDilation.collectAsState()
    val lensBlur by AppSettings.glassLensBlur.collectAsState()
    val chromaticEnabled by AppSettings.glassChromaticAberrationEnabled.collectAsState()
    val highlightEnabled by AppSettings.glassHighlightEnabled.collectAsState()

    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()

    val contentColor = MiuixTheme.colorScheme.onSurface
    val summaryColor = MiuixTheme.colorScheme.onSurfaceSecondary
    val onPrimary = MiuixTheme.colorScheme.onPrimary

    
    var hintTitle by remember { mutableStateOf("") }
    var hintText by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            
            LiquidSwitchPreference(
                checked = vibrancyEnabled,
                onCheckedChange = { AppSettings.setGlassVibrancyEnabled(it) },
                backdrop = backdrop,
                title = stringResource(R.string.settings_lg_vibrancy),
                summary = stringResource(R.string.settings_lg_vibrancy_desc),
                startAction = {
                    Icon(MiuixIcons.Theme, null, tint = contentColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            
            val blurTitle = stringResource(R.string.settings_lg_blur_radius)
            val blurHint = stringResource(R.string.settings_lg_hint_blur)
            LiquidSliderCard(
                backdrop = backdrop,
                title = blurTitle,
                value = { blurRadius },
                onValueChange = { AppSettings.setGlassBlurRadius(it) },
                contentColor = contentColor,
                summaryColor = summaryColor,
                onHint = { hintTitle = blurTitle; hintText = blurHint; showHint = true }
            )

            
            val dilTitle = stringResource(R.string.settings_lg_lens_dilation)
            val dilHint = stringResource(R.string.settings_lg_hint_lens_dilation)
            LiquidSliderCard(
                backdrop = backdrop,
                title = dilTitle,
                value = { lensDilation },
                onValueChange = { AppSettings.setGlassLensDilation(it) },
                contentColor = contentColor,
                summaryColor = summaryColor,
                onHint = { hintTitle = dilTitle; hintText = dilHint; showHint = true }
            )

            
            val lblurTitle = stringResource(R.string.settings_lg_lens_blur)
            val lblurHint = stringResource(R.string.settings_lg_hint_lens_blur)
            LiquidSliderCard(
                backdrop = backdrop,
                title = lblurTitle,
                value = { lensBlur },
                onValueChange = { AppSettings.setGlassLensBlur(it) },
                contentColor = contentColor,
                summaryColor = summaryColor,
                onHint = { hintTitle = lblurTitle; hintText = lblurHint; showHint = true }
            )

            
            LiquidSwitchPreference(
                checked = chromaticEnabled,
                onCheckedChange = { AppSettings.setGlassChromaticAberrationEnabled(it) },
                backdrop = backdrop,
                title = stringResource(R.string.settings_lg_chromatic_aberration),
                summary = stringResource(R.string.settings_lg_chromatic_aberration_desc),
                switchColors = SwitchDefaults.switchColors(
                    checkedThumbColor = onPrimary,
                    checkedTrackColor = MiuixTheme.colorScheme.primary
                ),
                startAction = {
                    Icon(MiuixIcons.Theme, null, tint = contentColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            
            LiquidSwitchPreference(
                checked = highlightEnabled,
                onCheckedChange = { AppSettings.setGlassHighlightEnabled(it) },
                backdrop = backdrop,
                title = stringResource(R.string.settings_lg_highlight),
                summary = stringResource(R.string.settings_lg_highlight_desc),
                switchColors = SwitchDefaults.switchColors(
                    checkedThumbColor = onPrimary,
                    checkedTrackColor = MiuixTheme.colorScheme.primary
                ),
                startAction = {
                    Icon(MiuixIcons.Theme, null, tint = contentColor, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            
            LiquidButton(
                onClick = {
                    AppSettings.setGlassVibrancyEnabled(true)
                    AppSettings.setGlassBlurRadius(2f)
                    AppSettings.setGlassLensDilation(12f)
                    AppSettings.setGlassLensBlur(24f)
                    AppSettings.setGlassChromaticAberrationEnabled(false)
                    AppSettings.setGlassHighlightEnabled(true)
                },
                backdrop = backdrop,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_lg_reset_defaults),
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(72.dp))
        }
    }

    
    LiquidWindowDialog(
        show = showHint,
        backdrop = backdrop,
        title = hintTitle,
        onDismissRequest = { showHint = false }
    ) {
        Text(
            text = hintText,
            fontSize = 14.sp,
            color = MiuixTheme.colorScheme.onSurfaceSecondary,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        LiquidButton(
            onClick = { showHint = false },
            backdrop = backdrop,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.dialog_confirm),
                color = MiuixTheme.colorScheme.primary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun LiquidSliderCard(
    backdrop: com.kyant.backdrop.Backdrop,
    title: String,
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    contentColor: Color,
    summaryColor: Color,
    onHint: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 16.sp, color = contentColor)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "%.1f dp".format(value()),
                    fontSize = 14.sp,
                    color = MiuixTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).clickable { onHint() },
                    tint = summaryColor
                )
            }
        }
        LiquidSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..50f,
            visibilityThreshold = 0.1f,
            backdrop = backdrop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
