package com.jetcomx.elaina.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object LogExporter {
    private const val TAG = "LogExporter"

    suspend fun export(context: Context): File? = withContext(Dispatchers.IO) {
        try {
            val logFile = File(context.cacheDir, "elaina_log_${System.currentTimeMillis()}.txt")
            val process = Runtime.getRuntime().exec("logcat -d")
            val allLogs = process.inputStream.bufferedReader().readText()
            process.waitFor()
            logFile.writeText(allLogs)
            Log.i(TAG, "日志已导出: ${logFile.absolutePath} (${logFile.length()} bytes)")
            logFile
        } catch (e: Exception) {
            Log.e(TAG, "导出日志失败", e)
            null
        }
    }

    fun getLogFileName(): String = "Elaina_Log.txt"

    fun saveLog(context: Context, uri: android.net.Uri) {
        try {
            val logFile = File(context.cacheDir, "elaina_log_temp.txt")
            val process = Runtime.getRuntime().exec("logcat -d")
            val allLogs = process.inputStream.bufferedReader().readText()
            process.waitFor()
            logFile.writeText(allLogs)
            context.contentResolver.openOutputStream(uri)?.use { out ->
                logFile.inputStream().use { it.copyTo(out) }
            }
            logFile.delete()
            Log.i(TAG, "日志已保存: $uri")
        } catch (e: Exception) {
            Log.e(TAG, "保存日志失败", e)
        }
    }

    fun shareLog(context: Context) {
        try {
            val logFile = File(context.cacheDir, "elaina_log_share.txt")
            val process = Runtime.getRuntime().exec("logcat -d")
            val allLogs = process.inputStream.bufferedReader().readText()
            process.waitFor()
            logFile.writeText(allLogs)
            share(context, logFile)
        } catch (e: Exception) {
            Log.e(TAG, "分享日志失败", e)
        }
    }

    fun share(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Log"))
        } catch (e: Exception) {
            Log.e(TAG, "分享日志失败", e)
        }
    }
}
