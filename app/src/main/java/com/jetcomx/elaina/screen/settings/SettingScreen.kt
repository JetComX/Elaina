package com.jetcomx.elaina.screen.settings

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.LensBlur
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetcomx.elaina.R
import com.jetcomx.elaina.navigation.LocalNavigator
import com.jetcomx.elaina.navigation.Route
import com.jetcomx.elaina.theme.MiuixThemeManager
import com.jetcomx.elaina.ui.component.LiquidArrowPreference
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LiquidGlassSnackbarHostImpl
import com.jetcomx.elaina.ui.component.LiquidOverlayDropdownPreference
import com.jetcomx.elaina.ui.component.LiquidSwitchPreference
import com.jetcomx.elaina.ui.component.LiquidWindowDialog
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.ui.component.LocalLiquidGlassSurface
import com.jetcomx.elaina.utils.AppSettings
import com.jetcomx.elaina.utils.CrashHandler
import com.jetcomx.elaina.utils.LogExporter
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.SwitchDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Background
import top.yukonga.miuix.kmp.icon.extended.Community
import top.yukonga.miuix.kmp.icon.extended.Create
import top.yukonga.miuix.kmp.icon.extended.ExpandLess
import top.yukonga.miuix.kmp.icon.extended.Image
import top.yukonga.miuix.kmp.icon.extended.Share
import top.yukonga.miuix.kmp.icon.extended.Theme
import top.yukonga.miuix.kmp.icon.extended.Tune
import top.yukonga.miuix.kmp.icon.extended.UploadCloud
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowDialog

private data class AccentOption(val name: String, val color: Color?)

@SuppressLint("LocalContextGetResourceValueCall")
@Preview
@Composable
fun SettingScreen() {
    val themeModeItems = listOf(
        stringResource(R.string.theme_system),
        stringResource(R.string.theme_dark),
        stringResource(R.string.theme_light),
        stringResource(R.string.theme_monet_system),
        stringResource(R.string.theme_monet_light),
        stringResource(R.string.theme_monet_dark)
    )

    val uiStyleItems = listOf(
        stringResource(R.string.settings_ui_style_one),
        stringResource(R.string.settings_ui_style_two),
    )

    val uiStyleModes = listOf(0, 1)
    val cardFeedbackItems = listOf(
        stringResource(R.string.card_feedback_none),
        stringResource(R.string.card_feedback_sink),
        stringResource(R.string.card_feedback_tilt)
    )

    val themeModes = listOf(
        ColorSchemeMode.System, ColorSchemeMode.Dark, ColorSchemeMode.Light,
        ColorSchemeMode.MonetSystem, ColorSchemeMode.MonetLight, ColorSchemeMode.MonetDark
    )
    val themeMode by MiuixThemeManager.mode.collectAsState()
    val themeModeIndex = themeModes.indexOf(themeMode).coerceAtLeast(0)
    val isMonet = themeMode in setOf(ColorSchemeMode.MonetSystem, ColorSchemeMode.MonetLight, ColorSchemeMode.MonetDark)
    val accentColor by MiuixThemeManager.accentColor.collectAsState()
    val showSnackbar by AppSettings.showSnackbar.collectAsState()
    val fastData by AppSettings.fastDataAcquisition.collectAsState()
    val cardFeedback by AppSettings.cardFeedback.collectAsState()
    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val debugMode by AppSettings.debugMode.collectAsState()
    val checkUpdate by AppSettings.checkUpdate.collectAsState()
    val currentShowSnackbar by rememberUpdatedState(showSnackbar)
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()

    val accentOptions = listOf(
        AccentOption(stringResource(R.string.accent_default), null),
        AccentOption(stringResource(R.string.accent_color_red), Color(0xFFF44336)),
        AccentOption(stringResource(R.string.accent_color_pink), Color(0xFFE91E63)),
        AccentOption(stringResource(R.string.accent_color_purple), Color(0xFF9C27B0)),
        AccentOption(stringResource(R.string.accent_color_deeppurple), Color(0xFF673AB7)),
        AccentOption(stringResource(R.string.accent_color_indigo), Color(0xFF3F51B5)),
        AccentOption(stringResource(R.string.accent_color_blue), Color(0xFF2196F3)),
        AccentOption(stringResource(R.string.accent_color_cyan), Color(0xFF00BCD4)),
        AccentOption(stringResource(R.string.accent_color_cyangreen), Color(0xFF009688)),
        AccentOption(stringResource(R.string.accent_color_green), Color(0xFF4FAF50)),
        AccentOption(stringResource(R.string.accent_color_yellow), Color(0xFFFFEB3B)),
        AccentOption(stringResource(R.string.accent_color_amber), Color(0xFFFFC107)),
        AccentOption(stringResource(R.string.accent_color_orange), Color(0xFFFF9800)),
        AccentOption(stringResource(R.string.accent_color_brown), Color(0xFF795548)),
        AccentOption(stringResource(R.string.accent_color_grayblue), Color(0xFF607D8F)),
        AccentOption(stringResource(R.string.accent_color_sakura), Color(0xFFFF9CA8)),

    )

    val accentItems = accentOptions.map { it.name }
    val accentSelectedIndex = when {
        accentColor == null -> 0
        else -> accentOptions.indexOfFirst { it.color == accentColor }.coerceAtLeast(0)
    }
    val switchedFormat = stringResource(R.string.settings_switched_to)
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()

    LaunchedEffect(Unit) { CrashHandler.logScreen("SettingScreen") }

    var showBgPicker by remember { mutableStateOf(false) }
    var bgConfirmed by remember { mutableStateOf(false) }
    val bgPath by AppSettings.backgroundImagePath.collectAsState()
    val hasBg = backgroundStyle == 2 && !bgPath.isNullOrEmpty()
    val bgStyleItems = listOf(
        stringResource(R.string.settings_bg_default),
        stringResource(R.string.settings_bg_dream_fluid),
        stringResource(R.string.settings_bg_custom_image),
    )

    val liquidSurface = LocalLiquidGlassSurface.current
    val cardColor = if (uiStyle == 1 && liquidSurface.isSpecified) liquidSurface
                    else MiuixTheme.colorScheme.surfaceContainerHighest
    val contentColor = MiuixTheme.colorScheme.onSurface
    val summaryColor = MiuixTheme.colorScheme.onSurfaceSecondary
    val onPrimary = if (uiStyle == 1) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onPrimary

    val navigator = LocalNavigator.current
    val context = LocalContext.current

    var showSendLogDialog by remember { mutableStateOf(false) }

    val saveLogLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            LogExporter.saveLog(context, uri)
            if (currentShowSnackbar) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.send_log_saved),
                        withDismissAction = true
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background,
        snackbarHost = {
            if (uiStyle == 1) {
                LiquidGlassSnackbarHostImpl(
                    hostState = snackbarHostState,
                    backdrop = backdrop,
                    modifier = Modifier
                )
            } else {
                SnackbarHost(state = snackbarHostState)
            }
        },
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(R.string.nav_setting),
                largeTitleColor = MiuixTheme.colorScheme.primary,
                titleColor = MiuixTheme.colorScheme.primary,
                color = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            if (uiStyle == 1) {
                
                item {
                    LiquidSwitchPreference(
                        checked = checkUpdate,
                        onCheckedChange = { AppSettings.setCheckUpdate(it) },
                        backdrop = backdrop,
                        title = stringResource(R.string.settings_check_update),
                        summary = stringResource(R.string.settings_check_update_desc),
                        startAction = {
                            Icon(MiuixIcons.UploadCloud, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }

                
                item {
                    LiquidOverlayDropdownPreference(
                        title = stringResource(R.string.settings_theme),
                        summary = stringResource(R.string.settings_theme_desc),
                        items = themeModeItems,
                        selectedIndex = themeModeIndex,
                        onSelectedIndexChange = {
                            MiuixThemeManager.setMode(themeModes[it])
                            val msg = switchedFormat.replace("%s", themeModeItems[it])
                            if (currentShowSnackbar) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg, withDismissAction = true)
                                }
                            }
                        },
                        backdrop = backdrop,
                        showValue = true,
                        startAction = {
                            Icon(MiuixIcons.Background, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    AnimatedVisibility(
                        visible = isMonet,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            LiquidOverlayDropdownPreference(
                                title = stringResource(R.string.settings_accent),
                                summary = stringResource(R.string.settings_accent_summary),
                                items = accentItems,
                                selectedIndex = accentSelectedIndex,
                                onSelectedIndexChange = {
                                    val option = accentOptions[it]
                                    MiuixThemeManager.setAccentColor(option.color)
                                    val msg = switchedFormat.replace("%s", option.name)
                                    if (currentShowSnackbar) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(msg, withDismissAction = true)
                                        }
                                    }
                                },
                                backdrop = backdrop,
                                showValue = true,
                                startAction = {
                                    Icon(MiuixIcons.Theme, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                    LiquidOverlayDropdownPreference(
                        title = stringResource(R.string.settings_ui_style),
                        summary = stringResource(R.string.settings_ui_style_desc),
                        items = uiStyleItems,
                        selectedIndex = uiStyle,
                        onSelectedIndexChange = {
                            AppSettings.setUiStyle(uiStyleModes[it])
                            val msg = switchedFormat.replace("%s", uiStyleItems[it])
                            if (currentShowSnackbar) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg, withDismissAction = true)
                                }
                            }
                        },
                        backdrop = backdrop,
                        showValue = true,
                        startAction = {
                            Icon(MiuixIcons.Create, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            LiquidArrowPreference(
                                title = stringResource(R.string.settings_liquidglass_customize),
                                summary = stringResource(R.string.settings_liquidglass_customize_desc),
                                backdrop = backdrop,
                                startAction = {
                                    Icon(Icons.Default.LensBlur, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                },
                                onClick = { navigator.push(Route.LiquidGlassCustomize) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                    LiquidOverlayDropdownPreference(
                        title = stringResource(R.string.settings_bg_style),
                        summary = stringResource(R.string.settings_bg_style_desc),
                        backdrop = backdrop,
                        items = bgStyleItems,
                        selectedIndex = backgroundStyle,
                        onSelectedIndexChange = {
                            AppSettings.setBackgroundStyle(it)
                            if (it == 2) { showBgPicker = true; bgConfirmed = false }
                        },
                        showValue = true,
                        startAction = {
                            Icon(MiuixIcons.Image, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }

                
                item {
                    LiquidSwitchPreference(
                        checked = fastData,
                        onCheckedChange = { AppSettings.setFastDataAcquisition(it) },
                        backdrop = backdrop,
                        title = stringResource(R.string.settings_fast_data),
                        summary = stringResource(R.string.settings_fast_data_desc),
                        startAction = {
                            Icon(MiuixIcons.Tune, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }

                
                item {
                    LiquidSwitchPreference(
                        checked = showSnackbar,
                        onCheckedChange = { AppSettings.setShowSnackbar(it) },
                        backdrop = backdrop,
                        title = stringResource(R.string.settings_show_snackbar),
                        summary = stringResource(R.string.settings_show_snackbar_desc),
                        switchColors = SwitchDefaults.switchColors(
                            checkedThumbColor = onPrimary,
                            checkedTrackColor = MiuixTheme.colorScheme.primary
                        ),
                        startAction = {
                            Icon(MiuixIcons.Community, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }

                
                item {
                    LiquidSwitchPreference(
                        checked = debugMode,
                        onCheckedChange = { AppSettings.setDebugMode(it) },
                        backdrop = backdrop,
                        title = stringResource(R.string.settings_debug_mode),
                        summary = stringResource(R.string.settings_debug_mode_desc),
                        startAction = {
                            Icon(Icons.Default.Adb, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    LiquidArrowPreference(
                        title = stringResource(R.string.settings_send_log),
                        summary = stringResource(R.string.settings_send_log_desc),
                        backdrop = backdrop,
                        startAction = {
                            Icon(MiuixIcons.Share, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        onClick = { showSendLogDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }

                
                item {
                    LiquidArrowPreference(
                        title = stringResource(R.string.settings_about),
                        summary = stringResource(R.string.settings_about_desc),
                        backdrop = backdrop,
                        startAction = {
                            Icon(Icons.Default.Person, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        onClick = { navigator.push(Route.About) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    LiquidArrowPreference(
                        title = stringResource(R.string.settings_credits),
                        summary = stringResource(R.string.settings_credits_desc),
                        backdrop = backdrop,
                        startAction = {
                            Icon(Icons.Default.ThumbUpAlt, null, tint = contentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                        },
                        onClick = { navigator.push(Route.Credits) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

            } else {
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.defaultColors(color = cardColor)
                    ) {
                        SwitchPreference(
                            checked = checkUpdate,
                            onCheckedChange = { AppSettings.setCheckUpdate(it) },
                            title = stringResource(R.string.settings_check_update),
                            summary = stringResource(R.string.settings_check_update_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            switchColors = SwitchDefaults.switchColors(
                                checkedThumbColor = onPrimary,
                                checkedTrackColor = MiuixTheme.colorScheme.primary
                            ),
                            startAction = {
                                Icon(MiuixIcons.UploadCloud, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        )
                    }
                }

                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.defaultColors(color = cardColor)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            WindowDropdownPreference(
                                title = stringResource(R.string.settings_theme),
                                summary = stringResource(R.string.settings_theme_desc),
                                titleColor = BasicComponentDefaults.titleColor(contentColor),
                                summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                                items = themeModeItems,
                                selectedIndex = themeModeIndex,
                                onSelectedIndexChange = {
                                    MiuixThemeManager.setMode(themeModes[it])
                                    val msg = switchedFormat.replace("%s", themeModeItems[it])
                                    if (currentShowSnackbar) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(msg, withDismissAction = true)
                                        }
                                    }
                                },
                                showValue = true,
                                startAction = {
                                    Icon(MiuixIcons.Background, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                            )

                            Spacer(Modifier.height(12.dp))
                            AnimatedVisibility(
                                visible = isMonet,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                WindowDropdownPreference(
                                    title = stringResource(R.string.settings_accent),
                                    summary = stringResource(R.string.settings_accent_summary),
                                    titleColor = BasicComponentDefaults.titleColor(contentColor),
                                    summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                                    items = accentItems,
                                    selectedIndex = accentSelectedIndex,
                                    onSelectedIndexChange = { index ->
                                        val option = accentOptions[index]
                                        MiuixThemeManager.setAccentColor(option.color)
                                        val msg = switchedFormat.replace("%s", option.name)
                                        if (currentShowSnackbar) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(msg, withDismissAction = true)
                                            }
                                        }
                                    },
                                    showValue = true,
                                    startAction = {
                                        Icon(MiuixIcons.Theme, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                )
                            }
                            WindowDropdownPreference(
                                title = stringResource(R.string.settings_ui_style),
                                summary = stringResource(R.string.settings_ui_style_desc),
                                titleColor = BasicComponentDefaults.titleColor(contentColor),
                                summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                                items = uiStyleItems,
                                selectedIndex = uiStyle,
                                onSelectedIndexChange = {
                                    AppSettings.setUiStyle(uiStyleModes[it])
                                    val msg = switchedFormat.replace("%s", uiStyleItems[it])
                                    if (currentShowSnackbar) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(msg, withDismissAction = true)
                                        }
                                    }
                                },
                                showValue = true,
                                startAction = {
                                    Icon(MiuixIcons.Create, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                            )
                            Spacer(Modifier.height(12.dp))
                            AnimatedVisibility(
                                visible = uiStyle == 1,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                ArrowPreference(
                                    title = stringResource(R.string.settings_liquidglass_customize),
                                    summary = stringResource(R.string.settings_liquidglass_customize_desc),
                                    titleColor = BasicComponentDefaults.titleColor(contentColor),
                                    summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                                    startAction = {
                                        Icon(MiuixIcons.Theme, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                    },
                                    onClick = { navigator.push(Route.LiquidGlassCustomize) }
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                            WindowDropdownPreference(
                                title = stringResource(R.string.settings_bg_style),
                                summary = stringResource(R.string.settings_bg_style_desc),
                                titleColor = BasicComponentDefaults.titleColor(contentColor),
                                summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                                items = bgStyleItems,
                                selectedIndex = backgroundStyle,
                                onSelectedIndexChange = {
                                    AppSettings.setBackgroundStyle(it)
                                    if (it == 2) { showBgPicker = true; bgConfirmed = false }
                                },
                                showValue = true,
                                startAction = {
                                    Icon(MiuixIcons.Image, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                            )
                        }
                    }
                }

                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.defaultColors(color = cardColor)
                    ) {
                        SwitchPreference(
                            checked = fastData,
                            onCheckedChange = { AppSettings.setFastDataAcquisition(it) },
                            title = stringResource(R.string.settings_fast_data),
                            summary = stringResource(R.string.settings_fast_data_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            switchColors = SwitchDefaults.switchColors(
                                checkedThumbColor = onPrimary,
                                checkedTrackColor = MiuixTheme.colorScheme.primary
                            ),
                            startAction = {
                                Icon(MiuixIcons.Tune, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        )
                    }
                }

                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.defaultColors(color = cardColor)
                    ) {
                        SwitchPreference(
                            checked = showSnackbar,
                            onCheckedChange = { AppSettings.setShowSnackbar(it) },
                            title = stringResource(R.string.settings_show_snackbar),
                            summary = stringResource(R.string.settings_show_snackbar_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            switchColors = SwitchDefaults.switchColors(
                                checkedThumbColor = onPrimary,
                                checkedTrackColor = MiuixTheme.colorScheme.primary
                            ),
                            startAction = {
                                Icon(MiuixIcons.Community, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        )
                        WindowDropdownPreference(
                            title = stringResource(R.string.settings_card_feedback),
                            summary = stringResource(R.string.settings_card_feedback_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            items = cardFeedbackItems,
                            selectedIndex = cardFeedback,
                            onSelectedIndexChange = {
                                AppSettings.setCardFeedback(it)
                                val msg = switchedFormat.replace("%s", cardFeedbackItems[it])
                                if (currentShowSnackbar) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(msg, withDismissAction = true)
                                    }
                                }
                            },
                            showValue = true,
                            startAction = {
                                Icon(MiuixIcons.ExpandLess, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        )
                    }
                }

                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.defaultColors(color = cardColor)
                    ) {
                        SwitchPreference(
                            checked = debugMode,
                            onCheckedChange = { AppSettings.setDebugMode(it) },
                            title = stringResource(R.string.settings_debug_mode),
                            summary = stringResource(R.string.settings_debug_mode_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            switchColors = SwitchDefaults.switchColors(
                                checkedThumbColor = onPrimary,
                                checkedTrackColor = MiuixTheme.colorScheme.primary
                            ),
                            startAction = {
                                Icon(Icons.Default.Adb, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        )
                        ArrowPreference(
                            title = stringResource(R.string.settings_send_log),
                            summary = stringResource(R.string.settings_send_log_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            startAction = {
                                Icon(MiuixIcons.Share, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            },
                            onClick = { showSendLogDialog = true }
                        )
                    }
                }

                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.defaultColors(color = cardColor)
                    ) {
                        ArrowPreference(
                            title = stringResource(R.string.settings_about),
                            summary = stringResource(R.string.settings_about_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            startAction = {
                                Icon(Icons.Default.Person, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            },
                            onClick = { navigator.push(Route.About) }
                        )
                        ArrowPreference(
                            title = stringResource(R.string.settings_credits),
                            summary = stringResource(R.string.settings_credits_desc),
                            titleColor = BasicComponentDefaults.titleColor(contentColor),
                            summaryColor = BasicComponentDefaults.summaryColor(summaryColor),
                            startAction = {
                                Icon(Icons.Default.ThumbUpAlt, null, tint = contentColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                            },
                            onClick = { navigator.push(Route.Credits) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }

            if (uiStyle == 1) {
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
        }
    }

    if (uiStyle == 1) {
        LiquidWindowDialog(
            show = showBgPicker,
            backdrop = backdrop,
            onDismissRequest = {
                showBgPicker = false
                if (!bgConfirmed) AppSettings.setBackgroundStyle(0)
            }
        ) {
            BackgroundPickerScreen(
                onBack = { showBgPicker = false },
                onConfirmed = { bgConfirmed = true },
            )
        }

        LiquidWindowDialog(
            show = showSendLogDialog,
            backdrop = backdrop,
            title = stringResource(R.string.send_log_dialog_title),
            onDismissRequest = { showSendLogDialog = false }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LiquidButton(
                    onClick = {
                        showSendLogDialog = false
                        saveLogLauncher.launch(LogExporter.getLogFileName())
                    },
                    backdrop = backdrop,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.send_log_save),
                        fontSize = 16.sp,
                        color = contentColor
                    )
                }
                LiquidButton(
                    onClick = {
                        showSendLogDialog = false
                        LogExporter.shareLog(context)
                    },
                    backdrop = backdrop,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.send_log_share),
                        fontSize = 16.sp,
                        color = contentColor
                    )
                }
            }
        }
    } else {
        WindowDialog(
            show = showBgPicker,
            title = "",
            backgroundColor = Color.Transparent,
            onDismissRequest = {
                showBgPicker = false
                if (!bgConfirmed) AppSettings.setBackgroundStyle(0)
            }
        ) {
            BackgroundPickerScreen(
                onBack = { showBgPicker = false },
                onConfirmed = { bgConfirmed = true },
            )
        }

        WindowDialog(
            show = showSendLogDialog,
            title = stringResource(R.string.send_log_dialog_title),
            backgroundColor = MiuixTheme.colorScheme.background,
            onDismissRequest = { showSendLogDialog = false }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().background(MiuixTheme.colorScheme.background),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        showSendLogDialog = false
                        saveLogLauncher.launch(LogExporter.getLogFileName())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.send_log_save))
                }
                Button(
                    onClick = {
                        showSendLogDialog = false
                        LogExporter.shareLog(context)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.send_log_share))
                }
            }
        }
    }

}
