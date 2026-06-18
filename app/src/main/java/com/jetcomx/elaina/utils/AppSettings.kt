package com.jetcomx.elaina.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object AppSettings {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _blurEnabled = MutableStateFlow(false)
    val blurEnabled: StateFlow<Boolean> = _blurEnabled.asStateFlow()

    private val _useFloatingNav = MutableStateFlow(false)
    val useFloatingNav: StateFlow<Boolean> = _useFloatingNav.asStateFlow()

    private val _showSnackbar = MutableStateFlow(true)
    val showSnackbar: StateFlow<Boolean> = _showSnackbar.asStateFlow()

    fun loadBlurEnabled(enabled: Boolean) { _blurEnabled.value = enabled }
    fun setBlurEnabled(enabled: Boolean) {
        _blurEnabled.value = enabled
        scope.launch { SettingsStore.saveBlurEnabled(enabled) }
    }

    fun loadUseFloatingNav(enabled: Boolean) { _useFloatingNav.value = enabled }
    fun setUseFloatingNav(enabled: Boolean) {
        _useFloatingNav.value = enabled
        scope.launch { SettingsStore.saveUseFloatingNav(enabled) }
    }

    fun loadShowSnackbar(enabled: Boolean) { _showSnackbar.value = enabled }
    fun setShowSnackbar(enabled: Boolean) {
        _showSnackbar.value = enabled
        scope.launch { SettingsStore.saveShowSnackbar(enabled) }
    }

    private val _uiStyle = MutableStateFlow(0)
    val uiStyle: StateFlow<Int> = _uiStyle.asStateFlow()

    fun loadUiStyle(style: Int) { _uiStyle.value = style.coerceIn(0, 1) }
    fun setUiStyle(style: Int) {
        _uiStyle.value = style.coerceIn(0, 1)
        scope.launch { SettingsStore.saveUiStyle(style) }
    }

    private val _fastDataAcquisition = MutableStateFlow(false)
    val fastDataAcquisition: StateFlow<Boolean> = _fastDataAcquisition.asStateFlow()

    fun loadFastDataAcquisition(enabled: Boolean) { _fastDataAcquisition.value = enabled }
    fun setFastDataAcquisition(enabled: Boolean) {
        _fastDataAcquisition.value = enabled
        scope.launch { SettingsStore.saveFastDataAcquisition(enabled) }
    }

    private val _backgroundStyle = MutableStateFlow(0)
    val backgroundStyle: StateFlow<Int> = _backgroundStyle.asStateFlow()

    fun loadBackgroundStyle(style: Int) { _backgroundStyle.value = style.coerceIn(0, 2) }
    fun setBackgroundStyle(style: Int) {
        _backgroundStyle.value = style.coerceIn(0, 2)
        scope.launch { SettingsStore.saveBackgroundStyle(style) }
    }

    private val _backgroundImagePath = MutableStateFlow<String?>(null)
    val backgroundImagePath: StateFlow<String?> = _backgroundImagePath.asStateFlow()

    fun loadBackgroundImagePath(path: String?) { _backgroundImagePath.value = path }
    fun setBackgroundImagePath(path: String?) {
        _backgroundImagePath.value = path
        scope.launch { SettingsStore.saveBackgroundImagePath(path) }
    }

    private val _debugMode = MutableStateFlow(false)
    val debugMode: StateFlow<Boolean> = _debugMode.asStateFlow()

    fun loadDebugMode(enabled: Boolean) { _debugMode.value = enabled }
    fun setDebugMode(enabled: Boolean) {
        _debugMode.value = enabled
        scope.launch { SettingsStore.saveDebugMode(enabled) }
    }

    private val _checkUpdate = MutableStateFlow(true)
    val checkUpdate: StateFlow<Boolean> = _checkUpdate.asStateFlow()

    fun loadCheckUpdate(enabled: Boolean) { _checkUpdate.value = enabled }
    fun setCheckUpdate(enabled: Boolean) {
        _checkUpdate.value = enabled
        scope.launch { SettingsStore.saveCheckUpdate(enabled) }
    }

    private val _verified = MutableStateFlow(CryptoStore.getAidsfgdifbsu())
    val verified: StateFlow<Boolean> = _verified.asStateFlow()

    fun setVerified(verified: Boolean) {
        _verified.value = verified
        CryptoStore.setAidsfgdifbsu(verified)
    }

    private val _cardFeedback = MutableStateFlow(2)
    val cardFeedback: StateFlow<Int> = _cardFeedback.asStateFlow()

    fun loadCardFeedback(type: Int) { _cardFeedback.value = type.coerceIn(0, 2) }
    fun setCardFeedback(type: Int) {
        _cardFeedback.value = type.coerceIn(0, 2)
        scope.launch { SettingsStore.saveCardFeedback(type) }
    }

    
    private val _glassVibrancyEnabled = MutableStateFlow(true)
    val glassVibrancyEnabled: StateFlow<Boolean> = _glassVibrancyEnabled.asStateFlow()

    private val _glassBlurRadius = MutableStateFlow(2f)
    val glassBlurRadius: StateFlow<Float> = _glassBlurRadius.asStateFlow()

    private val _glassLensDilation = MutableStateFlow(12f)
    val glassLensDilation: StateFlow<Float> = _glassLensDilation.asStateFlow()

    private val _glassLensBlur = MutableStateFlow(24f)
    val glassLensBlur: StateFlow<Float> = _glassLensBlur.asStateFlow()

    fun loadGlassVibrancyEnabled(enabled: Boolean) { _glassVibrancyEnabled.value = enabled }
    fun setGlassVibrancyEnabled(enabled: Boolean) {
        _glassVibrancyEnabled.value = enabled
        scope.launch { SettingsStore.saveGlassVibrancyEnabled(enabled) }
    }

    fun loadGlassBlurRadius(radius: Float) { _glassBlurRadius.value = radius.coerceIn(0f, 50f) }
    fun setGlassBlurRadius(radius: Float) {
        _glassBlurRadius.value = radius.coerceIn(0f, 50f)
        scope.launch { SettingsStore.saveGlassBlurRadius(radius) }
    }

    fun loadGlassLensDilation(dilation: Float) { _glassLensDilation.value = dilation.coerceIn(0f, 50f) }
    fun setGlassLensDilation(dilation: Float) {
        _glassLensDilation.value = dilation.coerceIn(0f, 50f)
        scope.launch { SettingsStore.saveGlassLensDilation(dilation) }
    }

    fun loadGlassLensBlur(blur: Float) { _glassLensBlur.value = blur.coerceIn(0f, 50f) }
    fun setGlassLensBlur(blur: Float) {
        _glassLensBlur.value = blur.coerceIn(0f, 50f)
        scope.launch { SettingsStore.saveGlassLensBlur(blur) }
    }

    private val _glassHighlightEnabled = MutableStateFlow(true)
    val glassHighlightEnabled: StateFlow<Boolean> = _glassHighlightEnabled.asStateFlow()

    private val _glassChromaticAberrationEnabled = MutableStateFlow(false)
    val glassChromaticAberrationEnabled: StateFlow<Boolean> = _glassChromaticAberrationEnabled.asStateFlow()

    fun loadGlassHighlightEnabled(enabled: Boolean) { _glassHighlightEnabled.value = enabled }
    fun setGlassHighlightEnabled(enabled: Boolean) {
        _glassHighlightEnabled.value = enabled
        scope.launch { SettingsStore.saveGlassHighlightEnabled(enabled) }
    }

    fun loadGlassChromaticAberrationEnabled(enabled: Boolean) { _glassChromaticAberrationEnabled.value = enabled }
    fun setGlassChromaticAberrationEnabled(enabled: Boolean) {
        _glassChromaticAberrationEnabled.value = enabled
        scope.launch { SettingsStore.saveGlassChromaticAberrationEnabled(enabled) }
    }

}
