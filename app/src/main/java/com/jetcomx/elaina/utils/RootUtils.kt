package com.jetcomx.elaina.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class RootManagerInfo(
    val type: String,
    val version: String?
)

object RootUtils {
    private const val TAG = "RootUtils"

    suspend fun checkRoot(): Boolean = withContext(Dispatchers.IO) {
        Log.i(TAG, "开始检查 Root 权限")
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            val hasRoot = output.contains("uid=0")
            Log.i(TAG, "Root 检查完成: $hasRoot")
            hasRoot
        } catch (e: Exception) {
            Log.e(TAG, "Root 检查异常", e)
            false
        }
    }

    fun isSuBinaryExists(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/sbin/su",
            "/vendor/bin/su",
            "/system/bin/.ext/.su"
        )
        val exists = paths.any { File(it).exists() }
        Log.i(TAG, "su 二进制存在: $exists")
        return exists
    }

    private fun suDirExists(path: String): Boolean = runCatching {
        val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "test -d $path && echo 1 || echo 0"))
        val out = p.inputStream.bufferedReader().readText().trim()
        p.waitFor()
        out == "1"
    }.getOrDefault(false)

    private fun suReadFile(path: String): String? = runCatching {
        val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat $path"))
        val out = p.inputStream.bufferedReader().readText().trim()
        p.waitFor()
        out.ifBlank { null }
    }.getOrNull()

    suspend fun getRootManagerInfo(): RootManagerInfo = withContext(Dispatchers.IO) {
        Log.i(TAG, "开始检测 Root 管理器")
        try {
            val ksuExists = suDirExists("/data/adb/ksu")
            val ksudExists = suDirExists("/data/adb/ksud")
            Log.i(TAG, "KSU目录: ksu=$ksuExists, ksud=$ksudExists")
            if (ksuExists || ksudExists) {
                val version = runCatching {
                    val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "ksud -V"))
                    val out = p.inputStream.bufferedReader().readText().trim()
                    val err = p.errorStream.bufferedReader().readText().trim()
                    p.waitFor()
                    Log.i(TAG, "ksud -V stdout=[$out] stderr=[$err]")
                    out.ifBlank { null }
                }.getOrNull()
                Log.i(TAG, "检测到 KernelSU: ${version ?: "未知版本"}")
                return@withContext RootManagerInfo("KernelSU", version)
            }
            val apExists = suDirExists("/data/adb/ap")
            Log.i(TAG, "APatch目录: $apExists")
            if (apExists) {
                val version = suReadFile("/data/adb/ap/version")
                Log.i(TAG, "检测到 APatch: ${version ?: "未知版本"}")
                return@withContext RootManagerInfo("APatch", version)
            }
            val magiskExists = suDirExists("/data/adb/magisk")
            Log.i(TAG, "Magisk目录: $magiskExists")
            if (magiskExists) {
                val version = runCatching {
                    val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "magisk -v"))
                    val out = p.inputStream.bufferedReader().readText().trim()
                    val err = p.errorStream.bufferedReader().readText().trim()
                    p.waitFor()
                    Log.i(TAG, "magisk -v stdout=[$out] stderr=[$err]")
                    out.ifBlank { null }
                }.getOrNull()
                Log.i(TAG, "检测到 Magisk: ${version ?: "未知版本"}")
                return@withContext RootManagerInfo("Magisk", version)
            }
            Log.i(TAG, "未检测到已知 Root 管理器")
            RootManagerInfo("None", null)
        } catch (e: Exception) {
            Log.e(TAG, "Root 管理器检测异常", e)
            RootManagerInfo("None", null)
        }
    }
}
