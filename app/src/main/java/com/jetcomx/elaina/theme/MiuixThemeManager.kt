package com.jetcomx.elaina.theme

import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.jetcomx.elaina.utils.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeColorSpec
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle

object MiuixThemeManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val allModes = ColorSchemeMode.entries.toTypedArray()

    private val _mode = MutableStateFlow(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ColorSchemeMode.MonetSystem
        else ColorSchemeMode.System
    )
    val mode: StateFlow<ColorSchemeMode> = _mode.asStateFlow()

    private val _accentColor = MutableStateFlow<androidx.compose.ui.graphics.Color?>(null)
    val accentColor: StateFlow<androidx.compose.ui.graphics.Color?> = _accentColor.asStateFlow()

    val isMonet: Boolean
        get() = _mode.value in monetModes

    private val monetModes = setOf(
        ColorSchemeMode.MonetSystem,
        ColorSchemeMode.MonetLight,
        ColorSchemeMode.MonetDark
    )

    

    fun buildController(): ThemeController {
        val currentMode = _mode.value
        val isMonetMode = currentMode in monetModes
        val keyColor = _accentColor.value  

        Log.d("AccentDebug", "MiuixThemeManager.buildController: mode=$currentMode isMonet=$isMonetMode keyColor=$keyColor")

        return if (isMonetMode) {
            ThemeController(
                currentMode,
                keyColor = keyColor,
                paletteStyle = ThemePaletteStyle.TonalSpot,
                colorSpec = ThemeColorSpec.Spec2025
            )
        } else {
            ThemeController(currentMode)
        }
    }

    

    fun loadMode(ordinal: Int) {
        if (ordinal in allModes.indices) {
            _mode.value = allModes[ordinal]
        }
    }

    fun loadAccentColor(color: androidx.compose.ui.graphics.Color?) {
        Log.d("AccentDebug", "MiuixThemeManager.loadAccentColor: $color")
        _accentColor.value = color
    }

    

    fun setMode(mode: ColorSchemeMode) {
        _mode.value = mode
        scope.launch { SettingsStore.saveThemeMode(mode.ordinal) }
    }

    fun setAccentColor(color: androidx.compose.ui.graphics.Color?) {
        Log.d("AccentDebug", "MiuixThemeManager.setAccentColor: $color")
        _accentColor.value = color
        scope.launch { SettingsStore.saveAccentColor(color) }
    }
}

@Composable
fun ElainaTheme(content: @Composable () -> Unit) {
    val currentMode by MiuixThemeManager.mode.collectAsState()
    val accent by MiuixThemeManager.accentColor.collectAsState()

    Log.d("AccentDebug", "ElainaTheme composed: mode=$currentMode accent=$accent")

    val controller = remember(currentMode, accent) {
        MiuixThemeManager.buildController()
    }

    MiuixTheme(controller = controller) {
        content()
    }
}
