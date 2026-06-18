package com.jetcomx.elaina.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.jetcomx.elaina.myconst.getVersionName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object UpdateChecker {
    private const val TAG = "UpdateChecker"
    private const val API_URL = "https://api.github.com/repos/JetComX/Elaina/releases/latest"
    private const val RELEASES_URL = "https://github.com/JetComX/Elaina/releases/tag"

    data class UpdateInfo(
        val hasUpdate: Boolean,
        val latestVersion: String = "",
        val updateLog: String = "",
        val downloadUrl: String = ""
    )

    private val _updateInfo = MutableStateFlow(UpdateInfo(false))
    val updateInfo: StateFlow<UpdateInfo> = _updateInfo.asStateFlow()

    suspend fun check(context: Context) {
        if (!isNetworkAvailable()) {
            Log.i(TAG, "No network, skipping update check")
            return
        }
        if (!AppSettings.checkUpdate.value) {
            Log.i(TAG, "Update check disabled by user")
            return
        }
        withContext(Dispatchers.IO) {
            try {
                val currentVersion = getVersionName(context)
                Log.i(TAG, "Current version: $currentVersion, checking latest...")

                val conn = URL(API_URL).openConnection() as java.net.HttpURLConnection
                conn.setRequestProperty("User-Agent", "Elaina-UpdateChecker")
                conn.setRequestProperty("Accept", "application/vnd.github+json")
                conn.instanceFollowRedirects = true
                conn.connectTimeout = 10_000
                conn.readTimeout = 10_000

                val code = conn.responseCode
                Log.i(TAG, "API response code: $code")

                if (code != 200) {
                    val errBody = conn.errorStream?.bufferedReader()?.readText() ?: ""
                    Log.e(TAG, "API error $code: $errBody")
                    return@withContext
                }

                val json = conn.inputStream.bufferedReader().readText()
                val obj = JSONObject(json)
                val tagName = obj.getString("tag_name")
                val latestVersion = tagName.trimStart('v')
                val updateLog = obj.optString("body", "")
                Log.i(TAG, "Latest: $latestVersion (tag: $tagName)")

                if (compareVersion(latestVersion, currentVersion) > 0) {
                    val releaseUrl = "$RELEASES_URL/$tagName"
                    _updateInfo.value = UpdateInfo(true, latestVersion, updateLog, releaseUrl)
                    Log.i(TAG, "New version found: $latestVersion")
                } else {
                    Log.i(TAG, "Already up to date ($currentVersion >= $latestVersion)")
                    _updateInfo.value = UpdateInfo(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Check update failed", e)
                _updateInfo.value = UpdateInfo(false)
            }
        }
    }

    fun openDownload(context: Context) {
        val url = _updateInfo.value.downloadUrl
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    fun dismissUpdate() {
        _updateInfo.value = UpdateInfo(false)
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val app = SettingsStore.appContext ?: return false
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            if (!hasInternet && !isValidated) {
                Log.w(TAG, "Network lacks INTERNET/VALIDATED caps, trying anyway")
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, "Network check failed: ${e.message}")
            false
        }
    }

    private fun compareVersion(latest: String, current: String): Int {
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLen = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l != c) return l.compareTo(c)
        }
        return 0
    }
}
