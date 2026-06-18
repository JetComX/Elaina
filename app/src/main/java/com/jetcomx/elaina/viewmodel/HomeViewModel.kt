package com.jetcomx.elaina.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jetcomx.elaina.utils.AppData
import com.jetcomx.elaina.utils.CpuCoreUsage
import com.jetcomx.elaina.utils.CpuMonitor
import com.jetcomx.elaina.utils.ModuleChecker
import com.jetcomx.elaina.utils.SystemInfoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    val isModuleInstalled: StateFlow<Boolean?> = AppData.isModuleInstalled

    private val _systemInfo = MutableStateFlow(
        if (AppData.loaded) AppData.systemInfo else null
    )
    val systemInfo: StateFlow<SystemInfoProvider.SystemInfo?> = _systemInfo.asStateFlow()

    private val _moduleBuildTime = MutableStateFlow(
        if (AppData.loaded) AppData.moduleBuildTime else null
    )
    val moduleBuildTime: StateFlow<String?> = _moduleBuildTime.asStateFlow()

    val cpuUsages: StateFlow<List<CpuCoreUsage>> = CpuMonitor.cpuUsages

    init {
        if (!AppData.loaded) {
            checkModuleStatus()
            loadSystemInfo()
        }
    }

    private fun loadSystemInfo() {
        viewModelScope.launch {
            _systemInfo.value = SystemInfoProvider.getSystemInfo(getApplication<Application>())
        }
    }

    fun checkModuleStatus() {
        viewModelScope.launch {
            Log.i(TAG, "开始检测 AppOpt 模块状态")
            val installed = ModuleChecker.isModuleInstalled()
            AppData.isModuleInstalled.value = installed
            if (installed) {
                _moduleBuildTime.value = ModuleChecker.getModuleBuildTime()
            }
            Log.i(TAG, "模块安装状态: $installed")
        }
    }
}
