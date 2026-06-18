package com.jetcomx.elaina.utils

import android.util.Log
import com.jetcomx.elaina.utils.AppListParser.toConfigLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ModuleChecker {
    private const val TAG = "ModuleChecker"
    const val MODULE_PATH = "/data/adb/modules/AppOpt"
    const val CONFIG_FILE = "$MODULE_PATH/applist.conf"

    suspend fun getModuleBuildTime(): String? = withContext(Dispatchers.IO) {
        try {
            val cmd = "su -c 'stat -c %Y ${MODULE_PATH}/module.prop 2>/dev/null'"
            val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            if (output.isNotBlank()) {
                val epochSeconds = output.toLongOrNull() ?: return@withContext null
                val date = Date(epochSeconds * 1000)
                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                return@withContext formatter.format(date)
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "获取模块构建时间失败", e)
            null
        }
    }

    suspend fun isModuleInstalled(): Boolean = withContext(Dispatchers.IO) {
        try {
            val cmd = "su -c 'test -d $MODULE_PATH && echo YES || echo NO'"
            val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            val installed = output.contains("YES")
            Log.i(TAG, "模块安装状态: $installed")
            installed
        } catch (e: Exception) {
            Log.e(TAG, "检查模块目录失败", e)
            false
        }
    }

    suspend fun readConfigFile(): String? = withContext(Dispatchers.IO) {
        try {
            val cmd = "su -c 'cat $CONFIG_FILE 2>/dev/null'"
            val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
            val content = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            if (exitCode != 0 || content.isBlank()) null else content
        } catch (e: Exception) {
            Log.e(TAG, "读取配置文件失败", e)
            null
        }
    }

    suspend fun getDaemonPath(): String? = withContext(Dispatchers.IO) {
        val candidates = listOf(
            "$MODULE_PATH/AppOpt",
            "$MODULE_PATH/appopt",
            "$MODULE_PATH/bin/AppOpt",
            "$MODULE_PATH/bin/appopt",
            "$MODULE_PATH/system/bin/AppOpt",
            "$MODULE_PATH/system/bin/appopt"
        )
        for (path in candidates) {
            val cmd = "su -c 'test -f $path && echo YES || echo NO'"
            val process = ProcessBuilder("sh", "-c", cmd).start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            if (output.contains("YES")) {
                Log.i(TAG, "找到守护进程: $path")
                return@withContext path
            }
        }
        Log.w(TAG, "未找到守护进程可执行文件")
        null
    }

    suspend fun appendConfigLine(line: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cmd = "su -c 'cat >> $CONFIG_FILE'"
            val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
            process.outputStream.write(line.toByteArray(Charsets.UTF_8))
            process.outputStream.flush()
            process.outputStream.close()
            process.waitFor() == 0
        } catch (e: Exception) {
            Log.e(TAG, "追加配置行失败", e)
            false
        }
    }

    suspend fun writeConfigFile(content: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val cmd = "su -c 'cat > $CONFIG_FILE'"
            val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
            process.outputStream.write(content.toByteArray(Charsets.UTF_8))
            process.outputStream.flush()
            process.outputStream.close()
            process.waitFor() == 0
        } catch (e: Exception) {
            Log.e(TAG, "写入配置文件失败", e)
            false
        }
    }

    suspend fun deleteRuleLine(rule: AppRule): Boolean = withContext(Dispatchers.IO) {
        try {
            val content = readConfigFile() ?: return@withContext false
            val targetLine = rule.toConfigLine()
            val sb = StringBuilder()
            var deleted = false
            content.lineSequence().forEach { line ->
                val trimmed = line.trim()
                if (trimmed == targetLine) {
                    deleted = true
                } else {
                    sb.appendLine(line)
                }
            }
            if (deleted) writeConfigFile(sb.toString()) else false
        } catch (e: Exception) {
            Log.e(TAG, "删除规则行失败", e)
            false
        }
    }

    suspend fun deletePackageLines(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val content = readConfigFile() ?: return@withContext false
            val sb = StringBuilder()
            var deleted = false
            content.lineSequence().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    sb.appendLine(line)
                } else {
                    val rule = AppListParser.parseLine(trimmed)
                    if (rule == null || rule.packageName != packageName) {
                        sb.appendLine(line)
                    } else {
                        deleted = true
                    }
                }
            }
            if (deleted) writeConfigFile(sb.toString()) else true
        } catch (e: Exception) {
            Log.e(TAG, "删除包规则失败", e)
            false
        }
    }
}
