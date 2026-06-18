package com.jetcomx.elaina.utils

import android.util.Log

object CrashHandler {
    @Volatile
    var currentScreen: String = ""
    @Volatile
    var lastAction: String = ""

    private const val TAG = "Elaina"

    fun init() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
            Log.e(TAG, "═══ APP CRASHED ═══")
            Log.e(TAG, "Screen : $currentScreen")
            Log.e(TAG, "Action : $lastAction")
            Log.e(TAG, "Thread : ${thread.name} (id=${thread.id})")
            Log.e(TAG, "Exception: ${ex.javaClass.name}: ${ex.message}")
            for (line in ex.stackTrace.take(15)) {
                Log.e(TAG, "  at $line")
            }
            val cause = ex.cause
            if (cause != null) {
                Log.e(TAG, "Caused by: ${cause.javaClass.name}: ${cause.message}")
                for (line in cause.stackTrace.take(8)) {
                    Log.e(TAG, "  at $line")
                }
            }
            Log.e(TAG, "═══════════════════")
            defaultHandler?.uncaughtException(thread, ex)
        }
    }

    fun logScreen(screen: String) {
        currentScreen = screen
        Log.i(TAG, "Screen: $screen")
    }

    fun logAction(action: String) {
        lastAction = action
        Log.i(TAG, "Action: $action")
    }
}
