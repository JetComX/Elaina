package com.jetcomx.elaina.ui.component

import androidx.compose.ui.unit.dp
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

fun BackdropEffectScope.standardGlassEffects() {
    val vibrancyEnabled = AppSettings.glassVibrancyEnabled.value
    val blurRadius = AppSettings.glassBlurRadius.value
    val lensDilation = AppSettings.glassLensDilation.value
    val lensBlur = AppSettings.glassLensBlur.value
    val chromaticAberration = AppSettings.glassChromaticAberrationEnabled.value

    if (vibrancyEnabled) vibrancy()
    blur(blurRadius.dp.toPx())
    lens(lensDilation.dp.toPx(), lensBlur.dp.toPx(), chromaticAberration = chromaticAberration)
}
