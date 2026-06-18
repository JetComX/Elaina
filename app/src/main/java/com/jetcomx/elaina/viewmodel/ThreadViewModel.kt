package com.jetcomx.elaina.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jetcomx.elaina.R
import com.jetcomx.elaina.utils.AppData
import com.jetcomx.elaina.utils.AppListParser
import com.jetcomx.elaina.utils.AppListParser.toConfigLine
import com.jetcomx.elaina.utils.AppRule
import com.jetcomx.elaina.utils.AppRuleGroup
import com.jetcomx.elaina.utils.ModuleChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThreadViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ThreadViewModel"
    }

    private val _ruleGroups = MutableStateFlow<List<AppRuleGroup>>(emptyList())
    val ruleGroups: StateFlow<List<AppRuleGroup>> = _ruleGroups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage.asStateFlow()

    private val _packageLookup = MutableStateFlow<PackageLookup?>(null)
    val packageLookup: StateFlow<PackageLookup?> = _packageLookup.asStateFlow()

    val isModuleInstalled: StateFlow<Boolean?> = AppData.isModuleInstalled

    init {
        loadConfig()
    }

    fun loadConfig() {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val content = ModuleChecker.readConfigFile()
                if (content.isNullOrBlank()) {
                    _errorMessage.value = ctx.getString(R.string.thread_unable_read_config)
                    _ruleGroups.value = emptyList()
                    return@launch
                }
                val rules = AppListParser.parse(content)
                val groups = AppListParser.groupByPackage(rules)
                val resolved = withContext(Dispatchers.IO) {
                    resolveLabelsAndIcons(groups)
                }

                val (installed, uninstalled) = resolved.partition { it.appLabel != null }
                if (uninstalled.isNotEmpty()) {
                    uninstalled.forEach { group ->
                        ModuleChecker.deletePackageLines(group.packageName)
                    }
                }
                _ruleGroups.value = installed
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load config", e)
                _errorMessage.value = ctx.getString(R.string.loading_failed, e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun resolveLabelsAndIcons(groups: List<AppRuleGroup>): List<AppRuleGroup> {
        val app = getApplication<Application>()
        val pm = app.packageManager
        val density = app.resources.displayMetrics.density
        val iconPx = (48 * density).toInt()

        return groups.map { group ->
            val (label, icon) = try {
                val appInfo = pm.getApplicationInfo(group.packageName, 0)
                val label = pm.getApplicationLabel(appInfo).toString()
                val icon = try {
                    val drawable = appInfo.loadIcon(pm)
                    val bmp = Bitmap.createBitmap(iconPx, iconPx, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bmp)
                    drawable.setBounds(0, 0, iconPx, iconPx)
                    drawable.draw(canvas)
                    bmp
                } catch (_: Exception) { null }
                Pair(label, icon)
            } catch (_: PackageManager.NameNotFoundException) {
                Pair(null, null)
            }
            group.copy(appLabel = label, appIcon = icon)
        }
    }

    fun addRule(packageName: String, processName: String?, threadName: String?, cpuAffinity: String) {
        val ctx = getApplication<Application>()
        viewModelScope.launch {
            try {
                val rule = AppRule(
                    packageName = packageName,
                    processName = processName?.ifBlank { null },
                    threadName = threadName?.ifBlank { null },
                    cpuAffinity = cpuAffinity
                )
                val success = ModuleChecker.appendConfigLine("\n" + rule.toConfigLine())
                if (success) {
                    _operationMessage.value = ctx.getString(R.string.thread_add_success)
                    loadConfig()
                } else {
                    _operationMessage.value = ctx.getString(R.string.thread_add_failed)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add rule", e)
                _operationMessage.value = ctx.getString(R.string.thread_add_failed)
            }
        }
    }

    fun deleteRule(rule: AppRule) {
        val ctx = getApplication<Application>()
        viewModelScope.launch {
            try {
                val success = ModuleChecker.deleteRuleLine(rule)
                if (success) {
                    _operationMessage.value = ctx.getString(R.string.thread_delete_success)
                    loadConfig()
                } else {
                    _operationMessage.value = ctx.getString(R.string.thread_delete_failed)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete rule", e)
                _operationMessage.value = ctx.getString(R.string.thread_delete_failed)
            }
        }
    }

    fun editRule(
        oldRule: AppRule,
        newPackage: String,
        newProcess: String?,
        newThread: String?,
        newAffinity: String
    ) {
        val ctx = getApplication<Application>()
        viewModelScope.launch {
            try {
                val deleted = ModuleChecker.deleteRuleLine(oldRule)
                if (!deleted) {
                    _operationMessage.value = ctx.getString(R.string.thread_add_failed)
                    return@launch
                }
                val rule = AppRule(
                    packageName = newPackage,
                    processName = newProcess?.ifBlank { null },
                    threadName = newThread?.ifBlank { null },
                    cpuAffinity = newAffinity
                )
                val added = ModuleChecker.appendConfigLine("\n" + rule.toConfigLine())
                if (added) {
                    _operationMessage.value = ctx.getString(R.string.thread_add_success)
                    loadConfig()
                } else {
                    _operationMessage.value = ctx.getString(R.string.thread_add_failed)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to edit rule", e)
                _operationMessage.value = ctx.getString(R.string.thread_add_failed)
            }
        }
    }

    fun deletePackage(packageName: String) {
        val ctx = getApplication<Application>()
        viewModelScope.launch {
            try {
                val success = ModuleChecker.deletePackageLines(packageName)
                if (success) {
                    _operationMessage.value = ctx.getString(R.string.thread_delete_success)
                    loadConfig()
                } else {
                    _operationMessage.value = ctx.getString(R.string.thread_delete_failed)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete package", e)
                _operationMessage.value = ctx.getString(R.string.thread_delete_failed)
            }
        }
    }

    fun lookupPackage(packageName: String) {
        if (packageName.isBlank()) {
            _packageLookup.value = null
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val app = getApplication<Application>()
                val pm = app.packageManager
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val label = pm.getApplicationLabel(appInfo).toString()
                val density = app.resources.displayMetrics.density
                val iconPx = (48 * density).toInt()
                val drawable = appInfo.loadIcon(pm)
                val bmp = Bitmap.createBitmap(iconPx, iconPx, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, iconPx, iconPx)
                drawable.draw(canvas)
                _packageLookup.value = PackageLookup(label, bmp)
            } catch (_: Exception) {
                _packageLookup.value = null
            }
        }
    }

    fun clearOperationMessage() {
        _operationMessage.value = null
    }
}

data class PackageLookup(val label: String, val icon: Bitmap)
