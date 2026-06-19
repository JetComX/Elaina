package com.jetcomx.elaina.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InterruptedIOException

object AppOptRunner {
    private const val TAG = "AppOpt-Runner"
    private var currentProcess: Process? = null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun start(
        daemonPath: String,
        configPath: String,
        interval: Int = 3
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "准备启动守护进程: $daemonPath")

            val cmd = "su -c '$daemonPath -c $configPath -s $interval'"
            val process = ProcessBuilder("sh", "-c", cmd)
                .redirectErrorStream(true)
                .start()

            currentProcess = process

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    while (true) {
                        try {
                            line = reader.readLine() ?: break
                        } catch (e: InterruptedIOException) {
                            break
                        }
                    }
                } catch (e: Exception) {
                    if (e !is InterruptedIOException) {
                        Log.e(TAG, "日志读取异常", e)
                    }
                }
                val exitCode = process.waitFor()
                Log.i(TAG, "appopt 进程退出，退出码: $exitCode")

                currentProcess = null
            }

            delay(500)
            if (!process.isAlive) {
                val exitCode = process.exitValue()
                Log.e(TAG, "启动失败，退出码: $exitCode")

                currentProcess = null
                return@withContext false
            }

            Log.i(TAG, "服务已启动")

            true
        } catch (e: Exception) {
            Log.e(TAG, "启动异常", e)
            false
        }
    }

    suspend fun stop(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "正在停止 appopt 服务")
            currentProcess?.destroy()
            Runtime.getRuntime().exec(arrayOf("su", "-c", "killall AppOpt")).waitFor()
            currentProcess = null
            Log.i(TAG, "appopt 服务已停止")

            true
        } catch (e: Exception) {
            Log.e(TAG, "停止异常", e)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isRunning(): Boolean = currentProcess?.isAlive == true
}
