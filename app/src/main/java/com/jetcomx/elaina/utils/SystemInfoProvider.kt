package com.jetcomx.elaina.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.jetcomx.elaina.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SystemInfoProvider {
    private const val TAG = "SystemInfoProvider"

    data class SystemInfo(
        val cpuCores: Int,
        val cpuThreads: Int,
        val deviceModel: String,
        val cpuManufacturer: String,
        val hardware: String,
        val rootManager: RootManagerInfo? = null
    )

    suspend fun getSystemInfo(context: Context): SystemInfo = withContext(Dispatchers.IO) {
        val cores = Runtime.getRuntime().availableProcessors()
        val threads = getCpuThreadCount()
        val model = Build.MODEL
        val hardware = Build.HARDWARE
        val manufacturer = getCpuManufacturer(context)
        val rootManager = RootUtils.getRootManagerInfo()
        SystemInfo(cores, threads, model, manufacturer, hardware, rootManager)
    }

    private suspend fun getCpuThreadCount(): Int = withContext(Dispatchers.IO) {
        try {
            val cmd = "su -c 'grep -c ^processor /proc/cpuinfo'"
            val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            output.toIntOrNull() ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "获取CPU线程数失败", e)
            0
        }
    }
    private suspend fun getCpuManufacturer(context: Context): String = withContext(Dispatchers.IO) {
        try {
            val cmd = "su -c 'grep -m1 \"vendor_id\" /proc/cpuinfo'"
            val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            if (output.isNotBlank()) {
                val parts = output.split(":")
                if (parts.size > 1) {
                    return@withContext parts[1].trim()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "读取 CPU 制造商失败", e)
        }
        val unknown = context.getString(R.string.cpu_unknown)
        if (Build.SUPPORTED_ABIS.isNotEmpty()) {
            if (Build.SUPPORTED_ABIS[0].contains("arm64")) "ARM" else unknown
        } else {
            unknown
        }
    }
}
