package com.jetcomx.elaina.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jetcomx.elaina.R
import com.jetcomx.elaina.utils.AppData
import com.jetcomx.elaina.utils.AppListParser
import com.jetcomx.elaina.utils.AppSettings
import com.jetcomx.elaina.utils.CryptoStore
import com.jetcomx.elaina.utils.CpuMonitor
import com.jetcomx.elaina.utils.ModuleChecker
import com.jetcomx.elaina.utils.RootUtils
import com.jetcomx.elaina.utils.SettingsStore
import com.jetcomx.elaina.utils.SystemInfoProvider
import com.jetcomx.elaina.utils.UpdateChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "LoadingViewModel"
    }

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _statusText = MutableStateFlow(getApplication<Application>().getString(R.string.loading_preparing))
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    private val _loadingComplete = MutableStateFlow(false)
    val loadingComplete: StateFlow<Boolean> = _loadingComplete.asStateFlow()

    private val _hasRoot = MutableStateFlow<Boolean?>(null)
    val hasRoot: StateFlow<Boolean?> = _hasRoot.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        startLoading()
    }

    fun retry() {
        _hasRoot.value = null
        _errorMessage.value = null
        _progress.value = 0f
        _loadingComplete.value = false
        startLoading()
    }

    private fun startLoading() {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            SettingsStore.init(ctx)
            CryptoStore.init(ctx)
            SettingsStore.load()
            UpdateChecker.check(ctx)
            val startTime = System.currentTimeMillis()
            try {
                _statusText.value = ctx.getString(R.string.loading_checking_root)
                _progress.value = 0.05f
                val root = RootUtils.checkRoot()
                _hasRoot.value = root
                _progress.value = 0.20f

                if (!root) {
                    _statusText.value = ctx.getString(R.string.no_root_permission)
                    _errorMessage.value = ""
                    val elapsed = System.currentTimeMillis() - startTime
                    if (elapsed < 800) delay(800 - elapsed)
                    return@launch
                }

                _statusText.value = ctx.getString(R.string.loading_checking_module)
                _progress.value = 0.25f
                AppData.isModuleInstalled.value = ModuleChecker.isModuleInstalled()
                _progress.value = 0.55f

                _statusText.value = ctx.getString(R.string.loading_getting_system_info)
                _progress.value = 0.60f
                AppData.systemInfo = SystemInfoProvider.getSystemInfo(ctx)
                _progress.value = 0.80f

                if (AppData.isModuleInstalled.value == true) {
                    _statusText.value = ctx.getString(R.string.loading_reading_config)
                    _progress.value = 0.85f
                    AppData.moduleBuildTime = ModuleChecker.getModuleBuildTime()
                    AppData.configContent = ModuleChecker.readConfigFile()
                    _progress.value = 0.90f
                }

                _statusText.value = ctx.getString(R.string.loading_thread_data)
                _progress.value = 0.92f
                val configContent = AppData.configContent
                if (!configContent.isNullOrBlank()) {
                    val rules = AppListParser.parse(configContent)
                    val packages = rules.map { it.packageName }.distinct()
                    var loaded = 0
                    val psOut = Runtime.getRuntime()
                        .exec(arrayOf("su", "-c", "ps -A"))
                        .inputStream.bufferedReader().readText()
                    for (pkg in packages) {
                        val pids = psOut.lines()
                            .filter { it.contains(pkg, ignoreCase = true) }
                            .mapNotNull { it.trim().split("\\s+".toRegex()).getOrNull(1)?.toIntOrNull() }
                        loaded += pids.size
                    }
                    Log.i("LoadingViewModel", "预发现 $loaded 个运行进程 (${packages.size} 个应用)")
                }
                _progress.value = 0.94f

                _statusText.value = ctx.getString(R.string.loading_sampling_cpu)
                _progress.value = 0.95f
                val fastMode = AppSettings.fastDataAcquisition.value
                val monitorStart = System.currentTimeMillis()
                CpuMonitor.startMonitoring(fastMode = fastMode)

                _statusText.value = ctx.getString(R.string.loading_preparing_cpu)
                val targetWait = if (fastMode) 700L else 3700L
                val monitorElapsed = System.currentTimeMillis() - monitorStart
                if (monitorElapsed < targetWait) delay(targetWait - monitorElapsed)

                _statusText.value = ctx.getString(R.string.loading_complete)
                _progress.value = 1.0f
                AppData.loaded = true
                _loadingComplete.value = true

            } catch (e: Exception) {
                Log.e(TAG, "加载失败", e)
                _errorMessage.value = ctx.getString(R.string.loading_failed, e.message)
            }
        }
    }
}
