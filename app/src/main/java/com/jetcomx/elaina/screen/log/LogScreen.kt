package com.jetcomx.elaina.screen.log

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetcomx.elaina.R
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LiquidCard
import com.jetcomx.elaina.ui.component.LiquidGlassSnackbarHostImpl
import com.jetcomx.elaina.ui.component.LiquidWindowDialog
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppSettings
import com.jetcomx.elaina.utils.CrashHandler
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.window.WindowDialog
import java.io.BufferedReader
import java.io.InputStreamReader

data class LogEntry(
    val id: Int,
    val timestamp: String,
    val pid: String,
    val tid: String,
    val level: String,
    val tag: String,
    val message: String,
    val raw: String
)

private val LOG_TAGS = arrayOf(
    "Elaina", "HomeViewModel", "ThreadViewModel", "LoadingViewModel",
    "CpuMonitor", "ModuleChecker", "RootUtils", "SystemInfoProvider",
    "AppOptService", "AppOptRunner", "CrashHandler"
)

private val LOG_COLORS = mapOf(
    "V" to Color(0xFFB0BEC5),
    "D" to Color(0xFF64B5F6),
    "I" to Color(0xFF81C784),
    "W" to Color(0xFFFFB74D),
    "E" to Color(0xFFE57373),
    "F" to Color(0xFFE53935),
)

private fun humanReadable(entry: LogEntry, context: Context): String {
    val msg = entry.message
    return when {
        msg.startsWith("═══") -> context.getString(R.string.log_crash_occurred)
        msg.startsWith("Screen :") -> context.getString(R.string.log_crash_screen, msg.removePrefix("Screen : ").trim())
        msg.startsWith("Action :") -> context.getString(R.string.log_crash_action, msg.removePrefix("Action : ").trim())
        msg.startsWith("Exception:") -> context.getString(R.string.log_crash_exception, msg.removePrefix("Exception: ").trim())
        msg.startsWith("Caused by:") -> context.getString(R.string.log_crash_cause, msg.removePrefix("Caused by: ").trim())
        msg.startsWith("Screen: ") -> context.getString(R.string.log_nav_screen, msg.removePrefix("Screen: "))
        msg.startsWith("Action: ") -> msg.removePrefix("Action: ")
        else -> msg
    }
}

@Composable
fun LogScreen() {
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val debugMode by AppSettings.debugMode.collectAsState()
    val primary = MiuixTheme.colorScheme.primary
    val contentColor = MiuixTheme.colorScheme.onSurface
    val chipColor = if (uiStyle == 1) MiuixTheme.colorScheme.onSurface else primary
    val chipErrorColor = MiuixTheme.colorScheme.error
    val scope = rememberCoroutineScope()
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()
    val rawLogs = remember { mutableStateListOf<LogEntry>() }
    val appPid = android.os.Process.myPid().toString()
    val logs = if (debugMode) rawLogs else rawLogs.filter { it.pid == appPid && LOG_TAGS.any { t -> it.tag.startsWith(t) } }
    val ctx = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scrollBehavior = MiuixScrollBehavior()

    var showClearDialog by remember { mutableStateOf(false) }
    var showRefreshDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar by AppSettings.showSnackbar.collectAsState()

    fun loadLogs() {
        isLoading = true
        errorMsg = null
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val process = Runtime.getRuntime().exec(
                        arrayOf("logcat", "-d", "-v", "threadtime")
                    )
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    val allLines = reader.readLines()
                    reader.close()
                    process.waitFor()

                    val regex = Regex(
                        "^(\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d+)\\s+(\\d+)\\s+(\\d+)\\s+([VDIWEF])\\s+(.+?):\\s+(.*)$"
                    )
                    var nextId = 0
                    allLines.mapNotNull { line ->
                        regex.matchEntire(line)?.destructured?.let { (time, pid, tid, lvl, tag, msg) ->
                            LogEntry(nextId++, time, pid, tid, lvl, tag, msg, line)
                        }
                    }
                }
                rawLogs.clear()
                rawLogs.addAll(result)
            } catch (e: Exception) {
                errorMsg = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun clearLogs() {
        scope.launch {
            withContext(Dispatchers.IO) {
                Runtime.getRuntime().exec(arrayOf("logcat", "-c"))
            }
            rawLogs.clear()
        }
    }

    fun throwNpe() {
        if (!debugMode) return
        CrashHandler.logAction("Debug: throw NPE")
        val nullString: String? = null
        nullString!!.length
    }

    fun copyLogs(context: Context) {
        val text = logs.joinToString("\n") { entry ->
            if (debugMode) {
                entry.raw
            } else {
                "[${entry.timestamp}] ${humanReadable(entry, context)}"
            }
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Elaina Logs", text))
        if (showSnackbar) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.log_copied),
                    withDismissAction = true
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        CrashHandler.logScreen("LogScreen")
        loadLogs()
    }

    
    if (showClearDialog) {
        if (uiStyle == 1) {
            LiquidWindowDialog(
                show = true,
                backdrop = backdrop,
                title = stringResource(R.string.log_clear_title),
                summary = stringResource(R.string.log_clear_message),
                onDismissRequest = { showClearDialog = false }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    LiquidButton(
                        onClick = { showClearDialog = false },
                        backdrop = backdrop,
                        modifier = Modifier.weight(1f),
                        pressFeedbackType = PressFeedbackType.None
                    ) {
                        Text(
                            stringResource(R.string.dialog_cancel),
                            color = contentColor
                        )
                    }
                    LiquidButton(
                        onClick = {
                            showClearDialog = false
                            clearLogs()
                        },
                        backdrop = backdrop,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.dialog_confirm), color = contentColor)
                    }
                }
            }
        } else {
            WindowDialog(
                show = true,
                title = stringResource(R.string.log_clear_title),
                summary = stringResource(R.string.log_clear_message),
                onDismissRequest = { showClearDialog = false }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    Button(
                        onClick = { showClearDialog = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MiuixTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                    Button(
                        onClick = {
                            showClearDialog = false
                            clearLogs()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.dialog_confirm))
                    }
                }
            }
        }
    }

    
    if (showRefreshDialog) {
        if (uiStyle == 1) {
            LiquidWindowDialog(
                show = true,
                backdrop = backdrop,
                title = stringResource(R.string.log_refresh_title),
                summary = stringResource(R.string.log_refresh_message),
                onDismissRequest = { showRefreshDialog = false }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    LiquidButton(
                        onClick = { showRefreshDialog = false },
                        backdrop = backdrop,
                        modifier = Modifier.weight(1f),
                        pressFeedbackType = PressFeedbackType.None
                    ) {
                        Text(
                            stringResource(R.string.dialog_cancel),
                            color = contentColor
                        )
                    }
                    LiquidButton(
                        onClick = {
                            showRefreshDialog = false
                            loadLogs()
                        },
                        backdrop = backdrop,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.dialog_confirm), color = contentColor)
                    }
                }
            }
        } else {
            WindowDialog(
                show = true,
                title = stringResource(R.string.log_refresh_title),
                summary = stringResource(R.string.log_refresh_message),
                onDismissRequest = { showRefreshDialog = false }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    Button(
                        onClick = { showRefreshDialog = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MiuixTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                    Button(
                        onClick = {
                            showRefreshDialog = false
                            loadLogs()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.dialog_confirm))
                    }
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
                title = stringResource(R.string.nav_log),
                largeTitleColor = primary,
                titleColor = primary,
                scrollBehavior = scrollBehavior,
                color = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background
            )
        },
        contentWindowInsets = WindowInsets.systemBars
            .add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionChip(
                    icon = Icons.Default.Refresh,
                    onClick = { showRefreshDialog = true },
                    uiStyle = uiStyle,
                    backdrop = backdrop,
                    color = chipColor,
                    modifier = Modifier.weight(1f)
                )
                ActionChip(
                    icon = Icons.Default.Delete,
                    onClick = { showClearDialog = true },
                    uiStyle = uiStyle,
                    backdrop = backdrop,
                    color = chipColor,
                    modifier = Modifier.weight(1f)
                )
                ActionChip(
                    icon = Icons.Default.ContentCopy,
                    onClick = {

                        copyLogs(ctx)
                    },
                    uiStyle = uiStyle,
                    backdrop = backdrop,
                    color = chipColor,
                    modifier = Modifier.weight(1f)
                )
                if (debugMode) {
                    ActionChip(
                        icon = Icons.Default.Warning,
                        onClick = { throwNpe() },
                        uiStyle = uiStyle,
                        backdrop = backdrop,
                        color = chipErrorColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (logs.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMsg ?: stringResource(R.string.log_empty),
                        color = contentColor,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs, key = { it.id }) { entry ->
                        val displayText = if (debugMode) entry.raw else humanReadable(entry, ctx)
                        if (uiStyle == 1) {
                            LogItemLiquid(
                                entry = entry,
                                displayText = displayText,
                                backdrop = backdrop
                            )
                        } else {
                            LogItemNormal(
                                entry = entry,
                                displayText = displayText
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ActionChip(
    icon: ImageVector,
    onClick: () -> Unit,
    uiStyle: Int,
    backdrop: com.kyant.backdrop.Backdrop,
    color: Color,
    modifier: Modifier = Modifier,
) {
    if (uiStyle == 1) {
        LiquidCard(
            onClick = onClick,
            backdrop = backdrop,
            modifier = modifier.height(44.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    } else {
        Card(
            modifier = modifier.height(44.dp),
            colors = CardDefaults.defaultColors(MiuixTheme.colorScheme.surfaceContainer),
            cornerRadius = 12.dp,
            onClick = onClick
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LogItemLiquid(
    entry: LogEntry,
    displayText: String,
    backdrop: com.kyant.backdrop.Backdrop
) {
    val logColor = LOG_COLORS[entry.level] ?: MiuixTheme.colorScheme.onSurface

    LiquidCard(
        onClick = {},
        backdrop = backdrop,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        pressFeedbackType = PressFeedbackType.None,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.level,
                    color = logColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = entry.tag,
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = entry.timestamp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayText,
                color = logColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }

}

@Composable
private fun LogItemNormal(entry: LogEntry, displayText: String) {
    val logColor = LOG_COLORS[entry.level] ?: MiuixTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.defaultColors(MiuixTheme.colorScheme.surfaceContainer),
        cornerRadius = 12.dp
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.level,
                    color = logColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = entry.tag,
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = entry.timestamp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayText,
                color = logColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
