package com.jetcomx.elaina.screen.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.jetcomx.elaina.R
import com.jetcomx.elaina.myconst.MODULE_URI
import com.jetcomx.elaina.myconst.getVersionName
import com.jetcomx.elaina.ui.component.CpuBarChart
import com.jetcomx.elaina.ui.component.CpuLineChart
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LiquidCard
import com.jetcomx.elaina.ui.component.LiquidGlassSnackbarHostImpl
import com.jetcomx.elaina.ui.component.LiquidWindowDialog
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppSettings
import com.jetcomx.elaina.utils.CpuCoreUsage
import com.jetcomx.elaina.utils.CrashHandler
import com.jetcomx.elaina.utils.ModuleChecker
import com.jetcomx.elaina.utils.SystemInfoProvider
import com.jetcomx.elaina.utils.UpdateChecker
import com.jetcomx.elaina.viewmodel.HomeViewModel
import com.jetcomx.elaina.viewmodel.ThreadViewModel
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Email
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
private fun HomeCard(
    uiStyle: Int,
    backdrop: Backdrop,
    primary: Color,
    feedbackType: PressFeedbackType = PressFeedbackType.Sink,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    if (uiStyle == 1) {
        LiquidCard(
            onClick = onClick,
            backdrop = backdrop,
            modifier = modifier,
            pressFeedbackType = feedbackType
        ) { content() }
    } else {
        Card(
            modifier = modifier,
            colors = CardDefaults.defaultColors(primary),
            pressFeedbackType = feedbackType,
            onClick = onClick
        ) { content() }
    }
}

@Composable
private fun BasicInfoCard(
    systemInfo: SystemInfoProvider.SystemInfo?,
    onPrimary: Color,
    uiStyle: Int,
    backdrop: Backdrop,
    primary: Color,
    feedbackType: PressFeedbackType = PressFeedbackType.Sink,
    onClick: () -> Unit
) {
    val insideMargin = if (uiStyle == 1) {
        PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    } else {
        PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    }
    val titleColor =
        BasicComponentColors(color = onPrimary, disabledColor = onPrimary.copy(alpha = 0.38f))
    val summaryColor = BasicComponentColors(
        color = onPrimary.copy(alpha = 0.7f),
        disabledColor = onPrimary.copy(alpha = 0.3f)
    )

    HomeCard(
        uiStyle = uiStyle, backdrop = backdrop, primary = primary,
        feedbackType = feedbackType, onClick = onClick, modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            BasicComponent(
                title = stringResource(R.string.device_model),
                summary = systemInfo?.deviceModel ?: "---",
                titleColor = titleColor,
                summaryColor = summaryColor,
                insideMargin = insideMargin
            )
            BasicComponent(
                title = stringResource(R.string.cpu_cores),
                summary = systemInfo?.cpuCores?.toString() ?: "---",
                titleColor = titleColor,
                summaryColor = summaryColor,
                insideMargin = insideMargin
            )
            BasicComponent(
                title = stringResource(R.string.cpu_threads),
                summary = systemInfo?.cpuThreads?.toString() ?: "---",
                titleColor = titleColor,
                summaryColor = summaryColor,
                insideMargin = insideMargin
            )
            BasicComponent(
                title = stringResource(R.string.cpu_manufacturer),
                summary = systemInfo?.cpuManufacturer ?: "---",
                titleColor = titleColor,
                summaryColor = summaryColor,
                insideMargin = insideMargin
            )
            BasicComponent(
                title = stringResource(R.string.hardware_manufacturer),
                summary = systemInfo?.hardware ?: "---",
                titleColor = titleColor,
                summaryColor = summaryColor,
                insideMargin = insideMargin
            )
        }
    }
}

@Composable
private fun CpuUsageChart(
    usages: List<CpuCoreUsage>,
    isLineChart: Boolean,
    onPrimary: Color,
    uiStyle: Int,
    backdrop: Backdrop,
    primary: Color,
    feedbackType: PressFeedbackType = PressFeedbackType.Sink,
    onToggleStyle: () -> Unit
) {
    HomeCard(
        uiStyle = uiStyle, backdrop = backdrop, primary = primary,
        feedbackType = feedbackType, onClick = onToggleStyle,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLineChart) 300.dp else 280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = stringResource(R.string.cpu_usage),
                style = MaterialTheme.typography.titleMedium,
                color = onPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (isLineChart) {
                CpuLineChart(usages, onPrimary, Modifier.weight(1f))
            } else {
                CpuBarChart(usages, onPrimary, Modifier.weight(1f))
            }
            Text(
                text = stringResource(R.string.tap_to_toggle_style),
                style = MaterialTheme.typography.bodySmall,
                color = onPrimary.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun MiniCard(
    uiStyle: Int, backdrop: Backdrop, primary: Color, onPrimary: Color,
    title: String, summary: String,
    feedbackType: PressFeedbackType = PressFeedbackType.Sink,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    drawableRes: Int? = null
) {
    val insideMargin = if (uiStyle == 1) {
        PaddingValues(horizontal = 2.dp, vertical = 3.dp)
    } else {
        PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    }
    val titleColor =
        BasicComponentColors(color = onPrimary, disabledColor = onPrimary.copy(alpha = 0.38f))
    val summaryColor = BasicComponentColors(
        color = onPrimary.copy(alpha = 0.7f),
        disabledColor = onPrimary.copy(alpha = 0.3f)
    )

    HomeCard(
        uiStyle = uiStyle, backdrop = backdrop, primary = primary,
        feedbackType = feedbackType, onClick = onClick, modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon, contentDescription = null,
                    tint = onPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
            }
            BasicComponent(
                title = title, summary = summary,
                titleColor = titleColor, summaryColor = summaryColor,
                insideMargin = insideMargin
            )
        }
        if (drawableRes != null) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = drawableRes,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .rotate(30f)
                        .alpha(0.25f)
                )
            }
        }
    }
}

@Composable
fun HomeDialog(
    show: Boolean,
    title: String? = null,
    summary: String? = null,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val currentBackdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()
    if (uiStyle == 1) {
        LiquidWindowDialog(
            show = show,
            title = title ?: "",
            onDismissRequest = onDismissRequest,
            backdrop = currentBackdrop,
            content = {
                summary?.let { Text(it, color = MiuixTheme.colorScheme.primary) }
                Spacer(Modifier.height(8.dp))
                content()
            }
        )
    } else {
        WindowDialog(
            show = show, title = title ?: "", onDismissRequest = onDismissRequest,
            titleColor = MiuixTheme.colorScheme.onPrimary,
            backgroundColor = MiuixTheme.colorScheme.primary,
            content = {
                summary?.let {
                    Text(
                        it,
                        color = MiuixTheme.colorScheme.onPrimary,
                        modifier = Modifier.offset(x = (18).dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                content()
            }
        )
    }
}

@Composable
fun HomeScreen(onNavigateToThread: () -> Unit = {}) {
    val viewModel: HomeViewModel = viewModel()
    val threadViewModel: ThreadViewModel = viewModel()
    val isModuleInstalled by viewModel.isModuleInstalled.collectAsState()
    val systemInfo by viewModel.systemInfo.collectAsState()
    val buildTime by viewModel.moduleBuildTime.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showWorkInfoDialog by remember { mutableStateOf(false) }
    var showUpdateLogDialog by remember { mutableStateOf(false) }
    var isLineChart by remember { mutableStateOf(false) }
    val cpuUsages by viewModel.cpuUsages.collectAsState()
    val ruleGroups by threadViewModel.ruleGroups.collectAsState()
    val scope = rememberCoroutineScope()
    val lineChart = stringResource(R.string.line_chart)
    val barChart = stringResource(R.string.bar_chart)
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar by AppSettings.showSnackbar.collectAsState()
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val currentShowSnackbar by rememberUpdatedState(showSnackbar)

    val scrollBehavior = MiuixScrollBehavior()

    val primary = MiuixTheme.colorScheme.primary
    val onPrimary = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onPrimary
    val dialogTextColor = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onSurface
    val cardFeedback by AppSettings.cardFeedback.collectAsState()
    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()
    val updateInfo by UpdateChecker.updateInfo.collectAsState()

    val feedbackType = when (cardFeedback) {
        0 -> PressFeedbackType.None
        2 -> PressFeedbackType.Tilt
        else -> PressFeedbackType.Sink
    }

    LaunchedEffect(Unit) { CrashHandler.logScreen("HomeScreen") }

    Scaffold(
        containerColor = if (customBg || uiStyle == 1) Color.Transparent else MiuixTheme.colorScheme.background,
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(R.string.nav_home),
                largeTitleColor = MiuixTheme.colorScheme.primary,
                titleColor = MiuixTheme.colorScheme.primary,
                color = if (customBg || uiStyle == 1) Color.Transparent else MiuixTheme.colorScheme.background,
                actions = {
                    IconButton(onClick = { onNavigateToThread() }) {
                        Icon(MiuixIcons.Email, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = {
            if (uiStyle == 1) {
                LiquidGlassSnackbarHostImpl(snackbarHostState, backdrop, Modifier)
            } else {
                SnackbarHost(state = snackbarHostState)
            }
        },
        contentWindowInsets = WindowInsets.systemBars
            .add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .overScrollVertical()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isModuleInstalled == true) {
                        
                        item {
                            val rm = systemInfo?.rootManager
                            val rootSummary = if (rm != null && rm.type != "None") {
                                if (rm.version != null) "${rm.type} (${rm.version})" else rm.type
                            } else "---"
                            val ctx = LocalContext.current
                            fun openRootManager() {
                                val pkg = when (rm?.type) {
                                    "Magisk" -> "com.topjohnwu.magisk"
                                    "KernelSU" -> "me.weishu.kernelsu"
                                    "APatch" -> "me.bmax.apatch"
                                    else -> null
                                }
                                pkg?.let {
                                    ctx.packageManager.getLaunchIntentForPackage(it)
                                }?.let { ctx.startActivity(it) }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HomeCard(
                                    uiStyle = uiStyle, backdrop = backdrop,
                                    primary = primary,
                                    feedbackType = feedbackType,
                                    onClick = { showWorkInfoDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    val margin = if (uiStyle == 1) {
                                        PaddingValues(horizontal = 2.dp, vertical = 3.dp)
                                    } else {
                                        PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                                    }
                                    BasicComponent(
                                        title = stringResource(R.string.home_working),
                                        summary = stringResource(R.string.version, getVersionName(LocalContext.current)),
                                        titleColor = BasicComponentColors(color = onPrimary, disabledColor = onPrimary.copy(alpha = 0.38f)),
                                        summaryColor = BasicComponentColors(color = onPrimary.copy(alpha = 0.7f), disabledColor = onPrimary.copy(alpha = 0.3f)),
                                        insideMargin = margin
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MiniCard(
                                        uiStyle = uiStyle, backdrop = backdrop,
                                        primary = primary, onPrimary = onPrimary,
                                        title = stringResource(R.string.root_manager),
                                        summary = rootSummary,
                                        feedbackType = feedbackType,
                                        onClick = { openRootManager() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    )
                                    MiniCard(
                                        uiStyle = uiStyle, backdrop = backdrop,
                                        primary = primary, onPrimary = onPrimary,
                                        title = stringResource(R.string.home_thread_management),
                                        summary = "${ruleGroups.size}",
                                        feedbackType = feedbackType,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        onClick = onNavigateToThread
                                    )
                                }
                            }
                        }

                        
                        item {
                            BasicInfoCard(
                                systemInfo = systemInfo, onPrimary = onPrimary,
                                uiStyle = uiStyle, backdrop = backdrop, primary = primary,
                                feedbackType = feedbackType, onClick = {}
                            )
                        }

                        
                        item {
                            CpuUsageChart(
                                usages = cpuUsages, isLineChart = isLineChart,
                                onPrimary = onPrimary,
                                uiStyle = uiStyle, backdrop = backdrop, primary = primary,
                                feedbackType = feedbackType,
                                onToggleStyle = {
                                    isLineChart = !isLineChart
                                    if (currentShowSnackbar) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                if (isLineChart) lineChart else barChart,
                                                withDismissAction = true
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        
                        item {
                            HomeCard(
                                uiStyle = uiStyle, backdrop = backdrop, primary = primary,
                                feedbackType = feedbackType,
                                onClick = { showDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null, tint = onPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = when (isModuleInstalled) {
                                                false -> stringResource(R.string.appopt_module_not_installed)
                                                null -> stringResource(R.string.detecting)
                                                else -> ""
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            color = onPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = when (isModuleInstalled) {
                                            false -> stringResource(R.string.click_card_for_details)
                                            null -> stringResource(R.string.detecting_module)
                                            else -> ""
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = onPrimary.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                    if (uiStyle == 1) {
                        item { Spacer(Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }

    
    if (showDialog) {
        HomeDialog(
            show = true,
            title = if (isModuleInstalled == false) stringResource(R.string.dialog_title_start_service) else "",
            summary = when {
                isModuleInstalled == true -> stringResource(R.string.service_running)
                else -> stringResource(R.string.module_not_installed_cannot_start)
            },
            onDismissRequest = { showDialog = false },
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (isModuleInstalled == false) {
                    Text(
                        text = stringResource(R.string.module_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = dialogTextColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MODULE_URI))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primary,
                            contentColor = onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.get_module))
                    }
                }
            }
        }
    }

    
    if (showWorkInfoDialog) {
        HomeDialog(
            show = true,
            summary = stringResource(R.string.service_running),
            onDismissRequest = { showWorkInfoDialog = false },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    stringResource(R.string.version, getVersionName(LocalContext.current)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onPrimary
                )
                Text(
                    stringResource(R.string.build_path, ModuleChecker.MODULE_PATH),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onPrimary
                )
                buildTime?.let {
                    Text(
                        stringResource(R.string.build_time, it),
                        style = MaterialTheme.typography.bodyMedium,
                        color = onPrimary
                    )
                }
            }
        }
    }
    
    if (updateInfo.hasUpdate) {
        val ctx = LocalContext.current
        val updateTitle = stringResource(R.string.update_available, updateInfo.latestVersion)
        HomeDialog(
            show = true,
            title = updateTitle,
            summary = updateInfo.updateLog,
            onDismissRequest = { UpdateChecker.dismissUpdate() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (uiStyle == 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiquidButton(
                            backdrop = backdrop,
                            onClick = {
                                UpdateChecker.openDownload(ctx)
                                UpdateChecker.dismissUpdate()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.update_download), color = dialogTextColor)
                        }
                        LiquidButton(
                            backdrop = backdrop,
                            onClick = { UpdateChecker.dismissUpdate() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.dialog_cancel), color = dialogTextColor)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                UpdateChecker.openDownload(ctx)
                                UpdateChecker.dismissUpdate()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = onPrimary, contentColor = primary)
                        ) {
                            Text(stringResource(R.string.update_download))
                        }
                        Button(
                            onClick = { UpdateChecker.dismissUpdate() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.dialog_cancel))
                        }
                    }
                }
            }
        }
        if (showUpdateLogDialog) {
            val updateLog = updateInfo.updateLog
            val title = stringResource(R.string.update_log_title)
            if (uiStyle == 1) {
                com.jetcomx.elaina.ui.component.LiquidWindowDialog(
                    show = true, backdrop = backdrop, title = title,
                    onDismissRequest = { showUpdateLogDialog = false },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                    ) {
                        Text(updateLog, fontSize = 14.sp, color = MiuixTheme.colorScheme.onSurface)
                    }
                }
            } else {
                top.yukonga.miuix.kmp.window.WindowDialog(
                    show = true, title = title,
                    backgroundColor = MiuixTheme.colorScheme.background,
                    onDismissRequest = { showUpdateLogDialog = false },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                    ) {
                        Text(updateLog, fontSize = 14.sp, color = MiuixTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
