package com.jetcomx.elaina.utils

import kotlinx.coroutines.flow.MutableStateFlow

object AppData {
    var loaded: Boolean = false
    val isModuleInstalled = MutableStateFlow<Boolean?>(null)
    var systemInfo: SystemInfoProvider.SystemInfo? = null
    var moduleBuildTime: String? = null
    var configContent: String? = null
}
