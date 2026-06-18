package com.jetcomx.elaina.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "elaina_settings")

object SettingsStore {
    private val KEY_THEME_MODE = intPreferencesKey("theme_mode")
    private val KEY_ACCENT_COLOR = longPreferencesKey("accent_color")
    private val KEY_ACCENT_MODE = intPreferencesKey("accent_mode")
    private val KEY_BLUR_ENABLED = booleanPreferencesKey("blur_enabled")
    private val KEY_USE_FLOATING_NAV = booleanPreferencesKey("use_floating_nav")
    private val KEY_SHOW_SNACKBAR = booleanPreferencesKey("show_snackbar")
    private val KEY_UI_STYLE = intPreferencesKey("ui_style")
    private val KEY_FAST_DATA = booleanPreferencesKey("fast_data_acquisition")
    private val KEY_BACKGROUND_STYLE = intPreferencesKey("background_style")
    private val KEY_BG_PATH = stringPreferencesKey("background_image_path")
    private val KEY_DEBUG_MODE = booleanPreferencesKey("debug_mode")
    private val KEY_CHECK_UPDATE = booleanPreferencesKey("check_update")
    private val KEY_CARD_FEEDBACK = intPreferencesKey("card_feedback")
    private val KEY_GLASS_VIBRANCY = booleanPreferencesKey("glass_vibrancy")
    private val KEY_GLASS_BLUR_RADIUS = floatPreferencesKey("glass_blur_radius")
    private val KEY_GLASS_LENS_DILATION = floatPreferencesKey("glass_lens_dilation")
    private val KEY_GLASS_LENS_BLUR = floatPreferencesKey("glass_lens_blur")
    private val KEY_GLASS_HIGHLIGHT = booleanPreferencesKey("glass_highlight")
    private val KEY_GLASS_CHROMATIC_ABERRATION = booleanPreferencesKey("glass_chromatic_aberration")

    @Volatile
    var appContext: Context? = null
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun requireContext(): Context =
        appContext ?: throw IllegalStateException("SettingsStore not initialized. Call init() first.")

    suspend fun load() {
        val ctx = requireContext()
        val prefs = ctx.dataStore.data.first()
        Log.d("AccentDebug", "SettingsStore.load: KEY_ACCENT_COLOR raw=${prefs[KEY_ACCENT_COLOR]}, KEY_ACCENT_MODE raw=${prefs[KEY_ACCENT_MODE]}")
        prefs[KEY_THEME_MODE]?.let { com.jetcomx.elaina.theme.MiuixThemeManager.loadMode(it) }
        prefs[KEY_ACCENT_COLOR]?.let { raw ->
            Log.d("AccentDebug", "SettingsStore.load: restoring accent color from raw=$raw → Color=${androidx.compose.ui.graphics.Color(raw.toULong())}")
            com.jetcomx.elaina.theme.MiuixThemeManager.loadAccentColor(androidx.compose.ui.graphics.Color(raw.toULong()))
        } ?: Log.d("AccentDebug", "SettingsStore.load: KEY_ACCENT_COLOR is null (no saved accent)")
        
        prefs[KEY_BLUR_ENABLED]?.let { AppSettings.loadBlurEnabled(it) }
        prefs[KEY_USE_FLOATING_NAV]?.let { AppSettings.loadUseFloatingNav(it) }
        prefs[KEY_SHOW_SNACKBAR]?.let { AppSettings.loadShowSnackbar(it) }
        prefs[KEY_UI_STYLE]?.let { AppSettings.loadUiStyle(it) }
        prefs[KEY_FAST_DATA]?.let { AppSettings.loadFastDataAcquisition(it) }
        prefs[KEY_BACKGROUND_STYLE]?.let { AppSettings.loadBackgroundStyle(it) }
        prefs[KEY_BG_PATH]?.let { AppSettings.loadBackgroundImagePath(it) }
        prefs[KEY_DEBUG_MODE]?.let { AppSettings.loadDebugMode(it) }
        prefs[KEY_CHECK_UPDATE]?.let { AppSettings.loadCheckUpdate(it) }
        prefs[KEY_CARD_FEEDBACK]?.let { AppSettings.loadCardFeedback(it) }
        prefs[KEY_GLASS_VIBRANCY]?.let { AppSettings.loadGlassVibrancyEnabled(it) }
        prefs[KEY_GLASS_BLUR_RADIUS]?.let { AppSettings.loadGlassBlurRadius(it) }
        prefs[KEY_GLASS_LENS_DILATION]?.let { AppSettings.loadGlassLensDilation(it) }
        prefs[KEY_GLASS_LENS_BLUR]?.let { AppSettings.loadGlassLensBlur(it) }
        prefs[KEY_GLASS_HIGHLIGHT]?.let { AppSettings.loadGlassHighlightEnabled(it) }
        prefs[KEY_GLASS_CHROMATIC_ABERRATION]?.let { AppSettings.loadGlassChromaticAberrationEnabled(it) }
    }

    suspend fun saveThemeMode(mode: Int) {
        requireContext().dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun saveAccentColor(color: androidx.compose.ui.graphics.Color?) {
        requireContext().dataStore.edit { prefs ->
            if (color != null) {
                prefs[KEY_ACCENT_COLOR] = color.value.toLong()
            } else {
                prefs.remove(KEY_ACCENT_COLOR)
            }
        }
    }

    suspend fun saveAccentMode(mode: Int) {
        requireContext().dataStore.edit { it[KEY_ACCENT_MODE] = mode }
    }

    suspend fun saveBlurEnabled(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_BLUR_ENABLED] = enabled }
    }

    suspend fun saveUseFloatingNav(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_USE_FLOATING_NAV] = enabled }
    }

    suspend fun saveShowSnackbar(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_SHOW_SNACKBAR] = enabled }
    }

    suspend fun saveUiStyle(style: Int) {
        requireContext().dataStore.edit { it[KEY_UI_STYLE] = style }
    }

    suspend fun saveFastDataAcquisition(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_FAST_DATA] = enabled }
    }

    suspend fun saveBackgroundStyle(style: Int) {
        requireContext().dataStore.edit { it[KEY_BACKGROUND_STYLE] = style }
    }

    suspend fun saveBackgroundImagePath(path: String?) {
        requireContext().dataStore.edit { prefs ->
            if (path != null) {
                prefs[KEY_BG_PATH] = path
            } else {
                prefs.remove(KEY_BG_PATH)
            }
        }
    }

    suspend fun saveDebugMode(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_DEBUG_MODE] = enabled }
    }

    suspend fun saveCheckUpdate(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_CHECK_UPDATE] = enabled }
    }

    suspend fun saveCardFeedback(type: Int) {
        requireContext().dataStore.edit { it[KEY_CARD_FEEDBACK] = type }
    }

    suspend fun saveGlassVibrancyEnabled(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_GLASS_VIBRANCY] = enabled }
    }

    suspend fun saveGlassBlurRadius(radius: Float) {
        requireContext().dataStore.edit { it[KEY_GLASS_BLUR_RADIUS] = radius }
    }

    suspend fun saveGlassLensDilation(dilation: Float) {
        requireContext().dataStore.edit { it[KEY_GLASS_LENS_DILATION] = dilation }
    }

    suspend fun saveGlassLensBlur(blur: Float) {
        requireContext().dataStore.edit { it[KEY_GLASS_LENS_BLUR] = blur }
    }

    suspend fun saveGlassHighlightEnabled(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_GLASS_HIGHLIGHT] = enabled }
    }

    suspend fun saveGlassChromaticAberrationEnabled(enabled: Boolean) {
        requireContext().dataStore.edit { it[KEY_GLASS_CHROMATIC_ABERRATION] = enabled }
    }

}
