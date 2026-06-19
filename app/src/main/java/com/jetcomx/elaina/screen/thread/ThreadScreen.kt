package com.jetcomx.elaina.screen.thread

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jetcomx.elaina.R
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LiquidCard
import com.jetcomx.elaina.ui.component.LiquidCircularProgressIndicator
import com.jetcomx.elaina.ui.component.LiquidDropdownImpl
import com.jetcomx.elaina.ui.component.LiquidGlassSnackbarHostImpl
import com.jetcomx.elaina.ui.component.LiquidInputTextField
import com.jetcomx.elaina.ui.component.LiquidListPopupColumn
import com.jetcomx.elaina.ui.component.LiquidTextField
import com.jetcomx.elaina.ui.component.LiquidWindowDialog
import com.jetcomx.elaina.ui.component.LiquidWindowListPopup
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppRule
import com.jetcomx.elaina.utils.AppRuleGroup
import com.jetcomx.elaina.utils.AppSettings
import com.jetcomx.elaina.utils.CrashHandler
import com.jetcomx.elaina.utils.GameThreadPresets
import com.jetcomx.elaina.utils.ThreadCpuInfo
import com.jetcomx.elaina.viewmodel.ThreadViewModel
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ConvertFile
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowCascadingListPopup
import top.yukonga.miuix.kmp.window.WindowDialog
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ThreadScreen() {
    val viewModel: ThreadViewModel = viewModel()
    val ruleGroups by viewModel.ruleGroups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val operationMessage by viewModel.operationMessage.collectAsState()

    val isModuleInstalled by viewModel.isModuleInstalled.collectAsState()
    val packageLookup by viewModel.packageLookup.collectAsState()

    val uiStyle by AppSettings.uiStyle.collectAsState()
    val cardFeedback by AppSettings.cardFeedback.collectAsState()
    val feedbackType = when (cardFeedback) {
        0 -> PressFeedbackType.None
        2 -> PressFeedbackType.Tilt
        else -> PressFeedbackType.Sink
    }
    val primary = MiuixTheme.colorScheme.primary
    val onPrimary = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onPrimary
    val contentColor = MiuixTheme.colorScheme.onSurface
    val error = MiuixTheme.colorScheme.error
    val onError = MiuixTheme.colorScheme.onError

    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()

    val threadCancelStr = stringResource(R.string.thread_cancel)
    val allThreadsStr = stringResource(R.string.all_threads)
    val threadCountStr = stringResource(R.string.thread_count)
    val onlyThreadsStr = stringResource(R.string.only_threads)
    val threadSortHighToLowStr = stringResource(R.string.thread_sort_high_to_low)
    val threadSortByCpuStr = stringResource(R.string.thread_sort_by_cpu)
    val threadSortLowToHighStr = stringResource(R.string.thread_sort_low_to_high)
    val threadActivityEmptyStr = stringResource(R.string.thread_activity_empty)
    val threadProcessStr = stringResource(R.string.thread_process)
    val threadPackageDescStr = stringResource(R.string.thread_package_desc)
    val threadAddRuleSummaryStr = stringResource(R.string.thread_add_rule_summary)
    val threadEditRuleTitleStr = stringResource(R.string.thread_edit_rule_title)
    val threadAddRuleTitleStr = stringResource(R.string.thread_add_rule_title)
    val threadDeleteConfirmTitleStr = stringResource(R.string.thread_delete_confirm_title)

    @Composable
    fun ThreadCard(
        onClick: () -> Unit = {},
        modifier: Modifier = Modifier,
        containerColor: Color = Color.Unspecified,
        content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
    ) {
        if (uiStyle == 1) {
            LiquidCard(
                onClick = onClick,
                backdrop = backdrop,
                modifier = modifier,
                tint = containerColor,
                pressFeedbackType = feedbackType
            ) { content() }
        } else {
            Card(
                modifier = modifier,
                colors = CardDefaults.defaultColors(
                    color = if (containerColor.isSpecified) containerColor
                    else MiuixTheme.colorScheme.surfaceContainerHighest
                ),
                pressFeedbackType = feedbackType,
                onClick = onClick
            ) { content() }
        }
    }

    @Composable
    fun ThreadButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        containerColor: Color = Color.Unspecified,
        content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
    ) {
        if (uiStyle == 1) {
            LiquidButton(
                onClick = onClick,
                backdrop = backdrop,
                modifier = modifier
            ) { content() }
        } else {
            val colors = if (containerColor.isSpecified) {
                ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = Color.White
                )
            } else {
                ButtonDefaults.buttonColors()
            }
            Button(
                onClick = onClick,
                modifier = modifier,
                colors = colors
            ) { content() }
        }
    }

    @Composable
    fun ThreadTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        singleLine: Boolean = true,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ) {
        if (uiStyle == 1) {
            var expanded by remember { mutableStateOf(false) }
            LiquidTextField(
                inputField = {
                    LiquidInputTextField(
                        text = value,
                        onChange = onValueChange,
                        backdrop = backdrop,
                        label = label,
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        keyboardOptions = keyboardOptions
                    )
                },
                onExpandedChange = { expanded = it },
                expanded = expanded,
                outsideEndAction = {
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                expanded = false
                            },
                        text = threadCancelStr,
                        color = MiuixTheme.colorScheme.primary
                    )
                }
            )
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = label,
                singleLine = singleLine,
                keyboardOptions = keyboardOptions
            )
        }
    }

    var fabVisible by remember { mutableStateOf(true) }

    val fabScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (consumed.y < -5f) fabVisible = false
                else if (consumed.y > 5f) fabVisible = true
                return Offset.Zero
            }
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newPkg by remember { mutableStateOf("") }
    var newProc by remember { mutableStateOf("") }
    var newThread by remember { mutableStateOf("") }
    var newAffinity by remember { mutableStateOf("") }
    var editingRule by remember { mutableStateOf<AppRule?>(null) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<AppRuleGroup?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    var showGameScanConfirm by remember { mutableStateOf(false) }
    var showGameScanResult by remember { mutableStateOf(false) }
    var scanResults by remember { mutableStateOf<List<GameScanResult>>(emptyList()) }
    var scanRunning by remember { mutableStateOf(false) }

    var showThreadDialog by remember { mutableStateOf(false) }
    var threadTarget by remember { mutableStateOf<AppRuleGroup?>(null) }
    var threadCpuPercent by remember { mutableStateOf(-1f) }
    var threadLoading by remember { mutableStateOf(false) }
    var threadTimeout by remember { mutableStateOf(false) }
    var threadForeground by remember { mutableStateOf(true) }
    var threadJob by remember { mutableStateOf<Job?>(null) }
    var threadList by remember { mutableStateOf<List<ThreadCpuInfo>>(emptyList()) }
    var threadProcessCount by remember { mutableStateOf(0) }
    var threadTotalCount by remember { mutableStateOf(0) }
    var showAllThreads by remember { mutableStateOf(true) }
    var sortDescending by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val scrollBehavior = MiuixScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar by AppSettings.showSnackbar.collectAsState()
    val currentShowSnackbar by rememberUpdatedState(showSnackbar)
    LaunchedEffect(Unit) { CrashHandler.logScreen("ThreadScreen") }

    LaunchedEffect(operationMessage) {
        operationMessage?.let {
            if (currentShowSnackbar) {
                snackbarHostState.showSnackbar(it, withDismissAction = true)
            }
            viewModel.clearOperationMessage()
        }
    }

    Scaffold(
        containerColor = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_thread),
                largeTitleColor = primary,
                titleColor = primary,
                scrollBehavior = scrollBehavior,
                color = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background
            )
        },

        floatingActionButton = {
            var expanded by remember { mutableStateOf(false) }
            val animatedSize by animateDpAsState(
                targetValue = if (expanded) 65.dp else 56.dp,
                label = "fabSize"
            )
            AnimatedVisibility(
                visible = !isLoading && fabVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 72.dp)
                ) {
                    if (uiStyle == 1) {
                        LiquidButton(
                            backdrop = backdrop,
                            onClick = { showGameScanConfirm = true },
                            modifier = Modifier.size(animatedSize)
                        ) {
                            Icon(Icons.Default.VideogameAsset, null, tint = contentColor)
                        }
                    } else {
                        FloatingActionButton(
                            onClick = { showGameScanConfirm = true },
                            containerColor = primary,
                            minWidth = animatedSize,
                            minHeight = animatedSize
                        ) {
                            Icon(Icons.Default.VideogameAsset, null, tint = contentColor)
                        }
                    }

                    if (uiStyle == 1) {
                        LiquidButton(
                            backdrop = backdrop,
                            onClick = {
                                expanded = !expanded
                                showAddDialog = true
                            },
                            modifier = Modifier.size(animatedSize)
                        ) {
                            Icon(Icons.Default.Add, null, tint = contentColor)
                        }
                    } else {
                        FloatingActionButton(
                            onClick = {
                                expanded = !expanded
                                showAddDialog = true
                            },
                            containerColor = primary,
                            minWidth = animatedSize,
                            minHeight = animatedSize
                        ) {
                            Icon(Icons.Default.Add, null, tint = contentColor)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!isLoading && isModuleInstalled == false) {
                    ThreadCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        containerColor = error
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.thread_service_not_running),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = onError
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.thread_start_service),
                                style = MaterialTheme.typography.bodySmall,
                                color = onError.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiStyle == 1) {
                                    LiquidCircularProgressIndicator(
                                        backdrop = backdrop,
                                        progress = null
                                    )
                                } else {
                                    InfiniteProgressIndicator(color = primary)
                                }
                            }
                        }

                        errorMessage != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = errorMessage!!,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MiuixTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        ruleGroups.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.thread_no_rules),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantActions
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scrollEndHaptic()
                                    .overScrollVertical()
                                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                                    .nestedScroll(fabScrollConnection),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = stringResource(R.string.thread_long_press_delete_rule),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = primary,
                                        modifier = Modifier.padding(bottom = 4.dp, start = 14.dp)
                                    )
                                }
                                items(ruleGroups, key = { it.packageName }) { group ->
                                    RuleGroupCard(
                                        group = group,
                                        cardColor = primary,
                                        contentColor = if (uiStyle == 1) primary else onPrimary,
                                        onClick = {
                                            editTarget = group
                                            showEditDialog = true
                                        },
                                        onLongClick = {
                                            deleteTarget = group.packageName
                                            showDeleteDialog = true
                                        },
                                        onThreadInfo = {
                                            threadTarget = group
                                            showThreadDialog = true
                                            threadLoading = true
                                            threadTimeout = false
                                            threadCpuPercent = -1f
                                            threadForeground = true
                                            threadJob?.cancel()
                                            threadJob = scope.launch(Dispatchers.IO) {
                                                try {
                                                    withTimeout(15000.milliseconds) {
                                                        val t0 = System.currentTimeMillis()
                                                        val psText = runCatching {
                                                            val p = Runtime.getRuntime()
                                                                .exec(arrayOf("su", "-c", "ps -A"))
                                                            p.inputStream.bufferedReader()
                                                                .readText()
                                                        }.getOrDefault("")
                                                        val pids = psText.lines()
                                                            .filter {
                                                                it.contains(
                                                                    group.packageName,
                                                                    ignoreCase = true
                                                                )
                                                            }
                                                            .mapNotNull {
                                                                it.trim().split("\\s+".toRegex())
                                                                    .getOrNull(1)?.toIntOrNull()
                                                            }
                                                        Log.i(
                                                            "ThreadDate",
                                                            "${group.packageName}: ${pids.size}PIDs"
                                                        )

                                                        threadProcessCount = pids.size
                                                        threadForeground = pids.any { pid ->
                                                            runCatching {
                                                                val p = Runtime.getRuntime().exec(
                                                                    arrayOf(
                                                                        "su",
                                                                        "-c",
                                                                        "cat /proc/$pid/oom_score_adj 2>/dev/null"
                                                                    )
                                                                )
                                                                val score =
                                                                    p.inputStream.bufferedReader()
                                                                        .readText().trim()
                                                                        .toIntOrNull() ?: 999
                                                                p.waitFor()
                                                                score <= 0
                                                            }.getOrDefault(false)
                                                        }
                                                        Log.d(
                                                            "ThreadDate",
                                                            "foreground=$threadForeground"
                                                        )

                                                        if (pids.isEmpty()) {
                                                            threadCpuPercent = -1f
                                                            threadList = emptyList()
                                                            threadTotalCount = 0
                                                        } else {
                                                            val script =
                                                                pids.joinToString("\n") { pid ->
                                                                    "for tid in /proc/$pid/task}; read -r comm < \"\$tid/comm\" 2>/dev/null; read -r stat < \"\$tid/stat\" 2>/dev/null; echo \"\$tid_num|\$comm|\$stat\"; done"
                                                                }

                                                            fun readThreadJiffies(): Map<Int, Pair<String, Long>> {
                                                                val out = runCatching {
                                                                    val p = Runtime.getRuntime()
                                                                        .exec(
                                                                            arrayOf(
                                                                                "su",
                                                                                "-c",
                                                                                script
                                                                            )
                                                                        )
                                                                    p.inputStream.bufferedReader()
                                                                        .readText()
                                                                }.getOrDefault("")
                                                                val map =
                                                                    mutableMapOf<Int, Pair<String, Long>>()
                                                                for (line in out.lines()) {
                                                                    if (line.isBlank()) continue
                                                                    val sep1 = line.indexOf('|')
                                                                    val sep2 =
                                                                        line.indexOf('|', sep1 + 1)
                                                                    if (sep1 < 0 || sep2 < 0) continue
                                                                    val tid =
                                                                        line.substring(0, sep1)
                                                                            .toIntOrNull()
                                                                            ?: continue
                                                                    val name = line.substring(
                                                                        sep1 + 1,
                                                                        sep2
                                                                    ).trim()
                                                                    val stat =
                                                                        line.substring(sep2 + 1)
                                                                    val close =
                                                                        stat.lastIndexOf(')')
                                                                    if (close < 0) continue
                                                                    val rest =
                                                                        stat.substring(close + 2)
                                                                            .split("\\s+".toRegex())
                                                                    if (rest.size < 13) continue
                                                                    val jiffies =
                                                                        (rest[11].toLongOrNull()
                                                                            ?: 0) + (rest[12].toLongOrNull()
                                                                            ?: 0)
                                                                    map[tid] = Pair(name, jiffies)
                                                                }
                                                                return map
                                                            }

                                                            val j1 = readThreadJiffies()
                                                            delay(800.milliseconds)
                                                            val t2 = System.currentTimeMillis()
                                                            val j2 = readThreadJiffies()
                                                            val intervalSec = (t2 - t0) / 1000f

                                                            var totalCpu = 0f
                                                            val threads =
                                                                mutableListOf<ThreadCpuInfo>()
                                                            for ((tid, pair2) in j2) {
                                                                val (name, j2Jiffies) = pair2
                                                                val j1Jiffies =
                                                                    j1[tid]?.second ?: continue
                                                                val delta =
                                                                    (j2Jiffies - j1Jiffies).coerceAtLeast(
                                                                        0
                                                                    )
                                                                val pct =
                                                                    delta.toFloat() / 100f / intervalSec * 100f
                                                                totalCpu += pct
                                                                if (delta > 0) Log.d(
                                                                    "ThreadDate",
                                                                    "tid=$tid name=$name delta=$delta cpu=${
                                                                        "%.2f".format(pct)
                                                                    }%"
                                                                )
                                                                threads.add(
                                                                    ThreadCpuInfo(
                                                                            tid,
                                                                            name,
                                                                            pct
                                                                        )
                                                                    )
                                                            }
                                                            threads.sortWith(compareByDescending<ThreadCpuInfo> { it.cpuPercent }.thenBy { it.tid })
                                                            threadList = threads
                                                            threadTotalCount = j2.size
                                                            Log.i(
                                                                "ThreadDate",
                                                                "init: rawTotal=${
                                                                    "%.1f".format(totalCpu)
                                                                } all=${threads.size}/${j2.size} j1=${j1.size} j2=${j2.size} interval=${
                                                                    "%.2f".format(
                                                                        intervalSec
                                                                    )
                                                                }s"
                                                            )

                                                            val smoothed =
                                                                if (threadCpuPercent < 0f) totalCpu
                                                                else threadCpuPercent * 0.6f + totalCpu * 0.4f
                                                            threadCpuPercent =
                                                                if (smoothed < 0.05f) 0f else smoothed
                                                            Log.i(
                                                                "ThreadDate",
                                                                "init: smooth=${
                                                                    "%.1f".format(smoothed)
                                                                } (${System.currentTimeMillis() - t0}ms)"
                                                            )
                                                        }
                                                    }
                                                } catch (_: Exception) {
                                                    Log.e(
                                                        "ThreadDate",
                                                        "timeout: ${group.packageName}"
                                                    )
                                                    threadTimeout = true
                                                }
                                                threadLoading = false
                                            }
                                        },
                                        uiStyle = uiStyle,
                                        backdrop = backdrop,
                                        feedbackType = feedbackType
                                    )
                                }
                                if (uiStyle == 1) {
                                    item { Spacer(Modifier.height(100.dp)) }
                                }
                            }
                        }
                    }
                }
            }

            if (uiStyle == 1) {
                LiquidGlassSnackbarHostImpl(
                    hostState = snackbarHostState,
                    backdrop = backdrop,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else {
                SnackbarHost(
                    state = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

        }
    }

    if (showAddDialog) {
        val dialogPrimary = MiuixTheme.colorScheme.primary
        val dialogOnPrimary = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onPrimary
        val isValid = packageLookup != null && newAffinity.isNotBlank()
        var showValidation by remember { mutableStateOf(false) }

        LaunchedEffect(newPkg) {
            delay(400)
            viewModel.lookupPackage(newPkg)
        }

        val isEditMode = editingRule != null

        val addEditContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    ThreadTextField(
                        value = newPkg,
                        onValueChange = { newPkg = it; showValidation = false },
                        label = stringResource(R.string.thread_package),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    val lookup = packageLookup
                    if (lookup != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            AppIcon(lookup.icon)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = lookup.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = dialogOnPrimary
                            )
                        }
                    } else if (newPkg.length >= 4) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.thread_package_not_found),
                            style = MaterialTheme.typography.bodySmall,
                            color = MiuixTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = threadPackageDescStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = dialogOnPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
                Column {
                    ThreadTextField(
                        value = newProc,
                        onValueChange = { newProc = it },
                        label = stringResource(R.string.thread_process_name),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Text(
                        text = stringResource(R.string.thread_process_name_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = dialogOnPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
                Column {
                    ThreadTextField(
                        value = newThread,
                        onValueChange = { newThread = it },
                        label = stringResource(R.string.thread_specific),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Text(
                        text = stringResource(R.string.thread_specific_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = dialogOnPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
                Column {
                    ThreadTextField(
                        value = newAffinity,
                        onValueChange = { newAffinity = it; showValidation = false },
                        label = stringResource(R.string.thread_affinity),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val chipModifier: (String) -> Modifier = {
                            Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(dialogOnPrimary.copy(alpha = 0.15f))
                                .clickable { newAffinity = it }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        }
                        Text(
                            text = stringResource(R.string.thread_affinity_0_3),
                            modifier = chipModifier("0-3"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = dialogOnPrimary
                        )
                        Text(
                            text = stringResource(R.string.thread_affinity_4_6),
                            modifier = chipModifier("4-6"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = dialogOnPrimary
                        )
                        Text(
                            text = stringResource(R.string.thread_affinity_7_9),
                            modifier = chipModifier("7-9"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = dialogOnPrimary
                        )
                        Text(
                            text = stringResource(R.string.thread_affinity_0_7),
                            modifier = chipModifier("0-7"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = dialogOnPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.thread_affinity_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = dialogOnPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    ThreadButton(
                        onClick = {
                            showAddDialog = false
                            editingRule = null
                            viewModel.lookupPackage("")
                        },
                        containerColor = dialogOnPrimary
                    ) {
                        Text(threadCancelStr, color = contentColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    ThreadButton(
                        onClick = {
                            if (isValid) {
                                val old = editingRule
                                if (old != null) {
                                    CrashHandler.logAction("Edit rule: $newPkg -> $newAffinity")
                                    viewModel.editRule(old, newPkg, newProc, newThread, newAffinity)
                                } else {
                                    CrashHandler.logAction("Add rule: $newPkg -> $newAffinity")
                                    viewModel.addRule(newPkg, newProc, newThread, newAffinity)
                                }
                                showAddDialog = false
                                editingRule = null
                                newPkg = ""
                                newProc = ""
                                newThread = ""
                                newAffinity = ""
                                viewModel.lookupPackage("")
                            } else {
                                showValidation = true
                            }
                        },
                        containerColor = dialogOnPrimary
                    ) {
                        Text(
                            if (isEditMode) stringResource(R.string.thread_update) else stringResource(
                                R.string.thread_add
                            ), color = contentColor
                        )
                    }
                }
            }
        }

        if (uiStyle == 1) {
            LiquidWindowDialog(
                show = true,
                backdrop = backdrop,
                title = if (isEditMode) threadEditRuleTitleStr else threadAddRuleTitleStr,
                summary = if (isEditMode) threadPackageDescStr else threadAddRuleSummaryStr,
                onDismissRequest = {
                    showAddDialog = false
                    editingRule = null
                    viewModel.lookupPackage("")
                }
            ) {
                addEditContent()
            }
        } else {
            WindowDialog(
                show = true,
                title = if (isEditMode) threadEditRuleTitleStr else threadAddRuleTitleStr,
                summary = if (isEditMode) threadPackageDescStr else threadAddRuleSummaryStr,
                backgroundColor = dialogPrimary,
                onDismissRequest = {
                    showAddDialog = false
                    editingRule = null
                    viewModel.lookupPackage("")
                }
            ) {
                addEditContent()
            }
        }
    }

    if (showDeleteDialog && deleteTarget != null) {
        val dialogBg = if (uiStyle == 1) primary else MiuixTheme.colorScheme.surface
        val dialogOnBg = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onSurface
        val deleteContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.thread_delete_warning),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = dialogOnBg
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    ThreadButton(
                        onClick = { showDeleteDialog = false; deleteTarget = null }
                    ) {
                        Text(threadCancelStr, color = contentColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    ThreadButton(
                        onClick = {
                            deleteTarget?.let {
                                CrashHandler.logAction("Delete package: $it")
                                viewModel.deletePackage(it)
                            }
                            showDeleteDialog = false
                            deleteTarget = null
                        }
                    ) {
                        Text(stringResource(R.string.thread_delete), color = contentColor)
                    }
                }
            }
        }
        if (uiStyle == 1) {
            LiquidWindowDialog(
                show = true,
                backdrop = backdrop,
                title = threadDeleteConfirmTitleStr,
                summary = stringResource(R.string.thread_delete_confirm_message, deleteTarget!!),
                onDismissRequest = { showDeleteDialog = false; deleteTarget = null }
            ) {
                deleteContent()
            }
        } else {
            WindowDialog(
                show = true,
                title = threadDeleteConfirmTitleStr,
                summary = stringResource(R.string.thread_delete_confirm_message, deleteTarget!!),
                backgroundColor = dialogBg,
                onDismissRequest = { showDeleteDialog = false; deleteTarget = null }
            ) {
                deleteContent()
            }
        }
    }

    if (showEditDialog && editTarget != null) {
        val group = editTarget!!
        val dialogPrimary = MiuixTheme.colorScheme.primary
        val dialogOnPrimary = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onPrimary

        val editContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppIcon(group.appIcon)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.appLabel ?: group.packageName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = dialogOnPrimary
                        )
                        if (group.appLabel != null) {
                            Text(
                                text = group.packageName,
                                style = MaterialTheme.typography.bodySmall,
                                color = dialogOnPrimary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.thread_rules),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = dialogOnPrimary.copy(alpha = 0.7f)
                )

                group.rules.forEach { rule ->
                    val label = when {
                        rule.processName != null && rule.threadName != null ->
                            "${rule.processName} / ${rule.threadName}"

                        rule.processName != null -> rule.processName
                        rule.threadName != null -> rule.threadName
                        else -> stringResource(R.string.thread_default)
                    }
                    val ruleBg = dialogOnPrimary.copy(alpha = 0.1f)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ruleBg)
                            .combinedClickable(
                                onClick = {
                                    editingRule = rule
                                    newPkg = rule.packageName
                                    newProc = rule.processName ?: ""
                                    newThread = rule.threadName ?: ""
                                    newAffinity = rule.cpuAffinity
                                    showEditDialog = false
                                    editTarget = null
                                    showAddDialog = true
                                },
                                onLongClick = {
                                    CrashHandler.logAction("Delete rule: ${rule.packageName}")
                                    viewModel.deleteRule(rule)
                                    showEditDialog = false
                                    editTarget = null
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = dialogOnPrimary
                            )
                            Text(
                                text = "→ ${rule.cpuAffinity}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = dialogOnPrimary.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = dialogOnPrimary.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    CrashHandler.logAction("Delete rule: ${rule.packageName}")
                                    viewModel.deleteRule(rule)
                                    showEditDialog = false
                                    editTarget = null
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ThreadButton(
                    onClick = {
                        val pkg = group.packageName
                        showEditDialog = false
                        editTarget = null
                        newPkg = pkg
                        newProc = ""
                        newThread = ""
                        newAffinity = ""
                        showAddDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = dialogOnPrimary.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.thread_add_another_rule), color = dialogOnPrimary)
                }
            }
        }
        if (uiStyle == 1) {
            LiquidWindowDialog(
                show = true,
                backdrop = backdrop,
                title = group.appLabel ?: group.packageName,
                summary = group.packageName,
                onDismissRequest = { showEditDialog = false; editTarget = null }
            ) {
                editContent()
            }
        } else {
            WindowDialog(
                show = true,
                title = group.appLabel ?: group.packageName,
                summary = group.packageName,
                backgroundColor = dialogPrimary,
                onDismissRequest = { showEditDialog = false; editTarget = null }
            ) {
                editContent()
            }
        }
    }

    
    if (showThreadDialog && threadTarget != null) {
        val group = threadTarget!!

        
        LaunchedEffect(showThreadDialog) {
            delay(1000)
            while (showThreadDialog) {
                if (threadLoading) {
                    delay(1000); continue
                }
                kotlinx.coroutines.withContext(Dispatchers.IO) {
                    try {
                        withTimeout(10000) {
                            val t0 = System.currentTimeMillis()
                            val psText = runCatching {
                                val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "ps -A"))
                                p.inputStream.bufferedReader().readText()
                            }.getOrDefault("")
                            val pids = psText.lines()
                                .filter { it.contains(group.packageName, ignoreCase = true) }
                                .mapNotNull {
                                    it.trim().split("\\s+".toRegex()).getOrNull(1)?.toIntOrNull()
                                }

                            threadProcessCount = pids.size
                            threadForeground = pids.any { pid ->
                                runCatching {
                                    val p = Runtime.getRuntime().exec(
                                        arrayOf(
                                            "su",
                                            "-c",
                                            "cat /proc/$pid/oom_score_adj 2>/dev/null"
                                        )
                                    )
                                    val score = p.inputStream.bufferedReader().readText().trim()
                                        .toIntOrNull() ?: 999
                                    p.waitFor()
                                    score <= 0
                                }.getOrDefault(false)
                            }
                            if (pids.isEmpty()) {
                                threadCpuPercent = -1f
                                threadList = emptyList()
                                threadTotalCount = 0
                            } else {
                                val script = pids.joinToString("\n") { pid ->
                                    "for tid in /proc/$pid/task}; read -r comm < \"\$tid/comm\" 2>/dev/null; read -r stat < \"\$tid/stat\" 2>/dev/null; echo \"\$tid_num|\$comm|\$stat\"; done"
                                }

                                fun readJ(): Map<Int, Pair<String, Long>> {
                                    val out = runCatching {
                                        val p =
                                            Runtime.getRuntime().exec(arrayOf("su", "-c", script))
                                        p.inputStream.bufferedReader().readText()
                                    }.getOrDefault("")
                                    val map = mutableMapOf<Int, Pair<String, Long>>()
                                    for (line in out.lines()) {
                                        if (line.isBlank()) continue
                                        val sep1 = line.indexOf('|')
                                        val sep2 = line.indexOf('|', sep1 + 1)
                                        if (sep1 < 0 || sep2 < 0) continue
                                        val tid = line.substring(0, sep1).toIntOrNull() ?: continue
                                        val name = line.substring(sep1 + 1, sep2).trim()
                                        val stat = line.substring(sep2 + 1)
                                        val close = stat.lastIndexOf(')')
                                        if (close < 0) continue
                                        val rest = stat.substring(close + 2).split("\\s+".toRegex())
                                        if (rest.size < 13) continue
                                        val jiffies = (rest[11].toLongOrNull()
                                            ?: 0) + (rest[12].toLongOrNull() ?: 0)
                                        map[tid] = Pair(name, jiffies)
                                    }
                                    return map
                                }

                                val j1 = readJ()
                                delay(800)
                                val t2 = System.currentTimeMillis()
                                val j2 = readJ()
                                val intervalSec = (t2 - t0) / 1000f
                                var totalCpu = 0f
                                val threads = mutableListOf<ThreadCpuInfo>()
                                for ((tid, pair2) in j2) {
                                    val (name, j2Jiffies) = pair2
                                    val j1Jiffies = j1[tid]?.second ?: continue
                                    val delta = (j2Jiffies - j1Jiffies).coerceAtLeast(0)
                                    val pct = delta.toFloat() / 100f / intervalSec * 100f
                                    totalCpu += pct
                                    if (delta > 0) Log.d(
                                        "ThreadDate",
                                        "tid=$tid name=$name delta=$delta cpu=${"%.2f".format(pct)}%"
                                    )
                                    threads.add(ThreadCpuInfo(tid, name, pct))
                                }
                                threads.sortWith(compareByDescending<ThreadCpuInfo> { it.cpuPercent }.thenBy { it.tid })
                                threadList = threads
                                threadTotalCount = j2.size
                                val smoothed = if (threadCpuPercent < 0f) totalCpu
                                else threadCpuPercent * 0.6f + totalCpu * 0.4f
                                threadCpuPercent = if (smoothed < 0.05f) 0f else smoothed
                                Log.i(
                                    "ThreadDate",
                                    "refresh: rawTotal=${"%.1f".format(totalCpu)} smooth=${
                                        "%.1f".format(smoothed)
                                    } all=${threads.size}/${j2.size} j1=${j1.size} j2=${j2.size} (${System.currentTimeMillis() - t0}ms)"
                                )
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            }
        }

        val threadContent: @Composable () -> Unit = {
            Log.d(
                "ThreadDate",
                "dialog: timeout=$threadTimeout loading=$threadLoading cpu=$threadCpuPercent"
            )
            if (threadTimeout) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.thread_timeout),
                        style = MaterialTheme.typography.bodyMedium,
                        color = error
                    )
                    Text(
                        stringResource(R.string.thread_timeout_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimary.copy(alpha = 0.6f)
                    )
                }
            } else if (threadLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiStyle == 1) {
                        LiquidCircularProgressIndicator(backdrop = backdrop, progress = null)
                    } else {
                        InfiniteProgressIndicator()
                    }
                    Text(
                        stringResource(R.string.thread_loading),
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimary
                    )
                }
            } else if (threadCpuPercent < 0f) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        threadActivityEmptyStr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = onPrimary
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "%.1f%%".format(threadCpuPercent),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (threadCpuPercent > 100f) error else onPrimary
                    )

                    if (!threadForeground) {
                        Text(
                            text = stringResource(R.string.thread_foreground_warning),
                            style = MaterialTheme.typography.labelSmall,
                            color = error.copy(alpha = 0.7f)
                        )
                    }

                    if (threadList.isNotEmpty()) {
                        val displayList = (if (showAllThreads) threadList
                            else threadList.filter { it.cpuPercent >= 0.01f })
                            .let { if (sortDescending) it else it.reversed() }
                        val activeCount = threadList.count { it.cpuPercent >= 0.01f }
                        val displayMaxCpu = displayList.firstOrNull()?.cpuPercent ?: 0f

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (showAllThreads) "${allThreadsStr} ${threadList.size} ${threadCountStr}"
                                    else "${onlyThreadsStr} ${displayList.size} ${threadCountStr}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = onPrimary.copy(alpha = 0.65f),
                            )
                            val showFilterPopup = remember { mutableStateOf(false) }
                            val optionSize = 4
                            Box {
                                Text(
                                    text = stringResource(R.string.thread_filter),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = onPrimary.copy(alpha = 0.55f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { showFilterPopup.value = true }
                                        .background(onPrimary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                                if (uiStyle == 1) {
                                    LiquidWindowListPopup(
                                        show = showFilterPopup.value,
                                        backdrop = backdrop,
                                        alignment = PopupPositionProvider.Align.End,
                                        onDismissRequest = { showFilterPopup.value = false },
                                    ) {
                                        LiquidListPopupColumn(backdrop = backdrop) {
                                            LiquidDropdownImpl(
                                                text = "${allThreadsStr} ${threadList.size} ${threadCountStr}",
                                                optionSize = optionSize,
                                                isSelected = showAllThreads,
                                                index = 0,
                                                onSelectedIndexChange = { showAllThreads = true; showFilterPopup.value = false },
                                            )
                                            LiquidDropdownImpl(
                                                text = "${onlyThreadsStr} ${activeCount} ${threadCountStr}",
                                                optionSize = optionSize,
                                                isSelected = !showAllThreads,
                                                index = 1,
                                                onSelectedIndexChange = { showAllThreads = false; showFilterPopup.value = false },
                                            )
                                            HorizontalDivider()
                                            LiquidDropdownImpl(
                                                text = threadSortHighToLowStr,
                                                optionSize = optionSize,
                                                isSelected = sortDescending,
                                                index = 2,
                                                onSelectedIndexChange = { sortDescending = true; showFilterPopup.value = false },
                                            )
                                            LiquidDropdownImpl(
                                                text = threadSortLowToHighStr,
                                                optionSize = optionSize,
                                                isSelected = !sortDescending,
                                                index = 3,
                                                onSelectedIndexChange = { sortDescending = false; showFilterPopup.value = false },
                                            )
                                        }
                                    }
                                } else {
                                    val filterEntries = remember(showAllThreads, sortDescending, threadList.size, activeCount) {
                                        listOf(
                                            DropdownEntry(
                                                items = listOf(
                                                    DropdownItem(text = "$allThreadsStr ${threadList.size} $threadCountStr", selected = showAllThreads, onClick = { showAllThreads = true }),
                                                    DropdownItem(text = "$onlyThreadsStr $activeCount $threadCountStr", selected = !showAllThreads, onClick = { showAllThreads = false }),
                                                ),
                                            ),
                                            DropdownEntry(
                                                items = listOf(
                                                    DropdownItem(
                                                        text = threadSortByCpuStr,
                                                        children = listOf(
                                                            DropdownItem(text = threadSortHighToLowStr, selected = sortDescending, onClick = { sortDescending = true }),
                                                            DropdownItem(text = threadSortLowToHighStr, selected = !sortDescending, onClick = { sortDescending = false }),
                                                        ),
                                                    ),
                                                ),
                                            ),
                                        )
                                    }
                                    WindowCascadingListPopup(
                                        show = showFilterPopup.value,
                                        entries = filterEntries,
                                        onDismissRequest = { showFilterPopup.value = false },
                                        collapseOnSelection = false,
                                    )
                                }
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(onPrimary.copy(alpha = 0.06f)),
                            contentPadding = PaddingValues(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(displayList, key = { it.tid }) { thread ->
                                ThreadCpuRow(
                                    info = thread,
                                    maxCpu = displayMaxCpu,
                                    onPrimary = onPrimary,
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(
                            if (!threadForeground) R.string.thread_hint_background
                            else if (threadCpuPercent < 0.05f) R.string.thread_hint_idle
                            else R.string.thread_hint_active
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = onPrimary.copy(alpha = 0.45f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.thread_data_source),
                        style = MaterialTheme.typography.labelSmall,
                        color = onPrimary.copy(alpha = 0.35f)
                    )
                }
            }
        }
        if (uiStyle == 1) {
            LiquidWindowDialog(
                show = true,
                backdrop = backdrop,
                title = group.appLabel ?: group.packageName,
                titleColor = onPrimary,
                summary = if (threadCpuPercent < 0f && !threadLoading)
                    threadActivityEmptyStr
                else
                    "$threadProcessCount ${threadProcessStr} | $threadTotalCount ${threadCountStr}",
                summaryColor = onPrimary,
                onDismissRequest = {
                    showThreadDialog = false; threadTarget = null; threadCpuPercent = -1f; threadList = emptyList()
                }
            ) {
                threadContent()
            }
        } else {
            WindowDialog(
                show = true,
                title = group.appLabel ?: group.packageName,
                summary = if (threadCpuPercent < 0f && !threadLoading)
                    threadActivityEmptyStr
                else
                    "$threadProcessCount ${threadProcessStr} | $threadTotalCount ${threadCountStr}",
                summaryColor = onPrimary,
                backgroundColor = primary,
                onDismissRequest = {
                    showThreadDialog = false; threadTarget = null; threadCpuPercent = -1f; threadList = emptyList()
                }
            ) {
                threadContent()
            }
        }
    }

    if (showGameScanConfirm) {
        val dialogPrimary = primary
        val dialogOnPrimary = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onPrimary
        val confirmContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.gamescan_confirm_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = dialogOnPrimary
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThreadButton(onClick = { showGameScanConfirm = false }, modifier = Modifier.weight(1f)) {
                        Text(threadCancelStr, color = contentColor)
                    }
                    ThreadButton(
                        onClick = {
                            showGameScanConfirm = false; scanRunning = true; showGameScanResult = true
                            scope.launch(Dispatchers.IO) {
                                val pm = context.packageManager
                                val results = mutableListOf<GameScanResult>()
                                GameThreadPresets.presets.forEach { (pkg, threads) ->
                                    try {
                                        val info = pm.getPackageInfo(pkg, 0)
                                        val appInfo = info.applicationInfo ?: return@forEach
                                        val label = pm.getApplicationLabel(appInfo).toString()
                                        val icon = try { pm.getApplicationIcon(pkg) } catch (_: Exception) { null }
                                        val bitmap = (icon as? android.graphics.drawable.BitmapDrawable)?.bitmap
                                        results.add(GameScanResult(pkg, label, bitmap, threads))
                                    } catch (_: PackageManager.NameNotFoundException) { }
                                }
                                scanResults = results; scanRunning = false
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.dialog_confirm), color = contentColor) }
                }
            }
        }
        if (uiStyle == 1) {
            LiquidWindowDialog(show = true, backdrop = backdrop, title = stringResource(R.string.gamescan_title), onDismissRequest = { showGameScanConfirm = false }) { confirmContent() }
        } else {
            WindowDialog(show = true, title = stringResource(R.string.gamescan_title), backgroundColor = dialogPrimary, onDismissRequest = { showGameScanConfirm = false }) { confirmContent() }
        }
    }

    if (showGameScanResult) {
        val dialogPrimary = primary
        val dialogOnPrimary = if (uiStyle == 1) primary else MiuixTheme.colorScheme.onPrimary
        val resultsContent: @Composable () -> Unit = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 400.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (scanRunning) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        if (uiStyle == 1) LiquidCircularProgressIndicator(backdrop = backdrop, progress = null)
                        else InfiniteProgressIndicator()
                    }
                } else if (scanResults.isEmpty()) {
                    Text(stringResource(R.string.gamescan_empty), color = dialogOnPrimary)
                } else {
                    scanResults.forEach { result ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(dialogOnPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .clickable {
                                    showGameScanResult = false
                                    newPkg = result.packageName; newProc = ""; newThread = result.threads.firstOrNull() ?: ""; newAffinity = ""
                                    showAddDialog = true
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(result.icon)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(text = result.appLabel, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = dialogOnPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(text = "${result.threads.size} ${stringResource(R.string.thread_count)}: ${result.threads.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = dialogOnPrimary.copy(alpha = 0.7f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                            Icon(Icons.Default.Add, null, tint = dialogOnPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                ThreadButton(onClick = { showGameScanResult = false; scanResults = emptyList() }, modifier = Modifier.fillMaxWidth()) { Text(threadCancelStr, color = contentColor) }
            }
        }
        if (uiStyle == 1) {
            LiquidWindowDialog(show = true, backdrop = backdrop, title = stringResource(R.string.gamescan_result_title), summary = if (!scanRunning && scanResults.isNotEmpty()) "${scanResults.size} ${stringResource(R.string.thread_process)}" else null, onDismissRequest = { showGameScanResult = false; scanResults = emptyList() }) { resultsContent() }
        } else {
            WindowDialog(show = true, title = stringResource(R.string.gamescan_result_title), backgroundColor = dialogPrimary, onDismissRequest = { showGameScanResult = false; scanResults = emptyList() }) { resultsContent() }
        }
    }
}

@Composable
private fun RuleGroupCard(
    group: AppRuleGroup,
    cardColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onThreadInfo: () -> Unit,
    uiStyle: Int,
    backdrop: Backdrop,
    feedbackType: PressFeedbackType = PressFeedbackType.Sink
) {
    if (uiStyle == 1) {
        LiquidCard(
            onClick = {},
            backdrop = backdrop,
            modifier = Modifier.fillMaxWidth(),
            pressFeedbackType = PressFeedbackType.None,
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier.combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
            ) {
                RuleGroupContent(group, contentColor, onThreadInfo)
            }
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            pressFeedbackType = PressFeedbackType.None,
            colors = CardDefaults.defaultColors(color = cardColor),
            onClick = {}
        ) {
            Box(
                modifier = Modifier.combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
            ) {
                RuleGroupContent(group, contentColor, onThreadInfo)
            }
        }
    }
}

@Composable
private fun RuleGroupContent(
    group: AppRuleGroup,
    contentColor: Color,
    onThreadInfo: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppIcon(icon = group.appIcon)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.appLabel ?: group.packageName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (group.appLabel != null) {
                    Text(
                        text = group.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onThreadInfo) {
                Icon(
                    MiuixIcons.ConvertFile,
                    contentDescription = stringResource(R.string.thread_activity),
                    tint = contentColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (group.rules.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.thread_rules),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            group.rules.forEach { rule ->
                val label = when {
                    rule.processName != null && rule.threadName != null ->
                        "${rule.processName} / ${rule.threadName}"

                    rule.processName != null -> rule.processName
                    rule.threadName != null -> rule.threadName
                    else -> stringResource(R.string.thread_default)
                }
                RuleChip(label = label, value = rule.cpuAffinity, contentColor = contentColor)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun RuleChip(
    label: String,
    value: String,
    contentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(contentColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "→ $value",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@Composable
private fun ThreadCpuRow(
    info: ThreadCpuInfo,
    maxCpu: Float,
    onPrimary: Color,
) {
    val barFractionRaw = if (maxCpu > 0f) (info.cpuPercent / maxCpu).coerceIn(0f, 1f) else 0f
    val barFraction by animateFloatAsState(targetValue = barFractionRaw, label = "bar")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = info.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "%.1f%%".format(info.cpuPercent),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = if (info.cpuPercent > 50f) MiuixTheme.colorScheme.error else onPrimary
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(onPrimary.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(barFraction)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(onPrimary.copy(alpha = 0.45f))
            )
        }
    }
}

private data class GameScanResult(
    val packageName: String,
    val appLabel: String,
    val icon: Bitmap?,
    val threads: List<String>
)

@Composable
private fun AppIcon(icon: Bitmap?) {
    val placeholderColor = MiuixTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.10f)
    if (icon != null) {
        Image(
            bitmap = icon.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(placeholderColor, RoundedCornerShape(12.dp))
        )
    }
}
