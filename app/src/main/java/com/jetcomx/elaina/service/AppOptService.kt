package com.jetcomx.elaina.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jetcomx.elaina.MainActivity
import com.jetcomx.elaina.R
import com.jetcomx.elaina.utils.ModuleChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppOptService : Service() {

    companion object {
        private const val TAG = "AppOpt-Service"
        const val CHANNEL_ID = "appopt_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "com.jetcomx.elaina.START"
        const val ACTION_STOP = "com.jetcomx.elaina.STOP"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                Log.i(TAG, "处理启动指令")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(NOTIFICATION_ID, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
                } else {
                    startForeground(NOTIFICATION_ID, createNotification())
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val daemonPath = ModuleChecker.getDaemonPath()
                    if (daemonPath == null) {
                        Log.e(TAG, "未找到守护进程可执行文件，无法启动")
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        return@launch
                    }
                    val configPath = ModuleChecker.CONFIG_FILE
                    val success = AppOptRunner.start(
                        daemonPath = daemonPath,
                        configPath = ModuleChecker.CONFIG_FILE,
                    )
                    if (!success) {
                        Log.e(TAG, "启动守护进程失败")
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
            ACTION_STOP -> {
                Log.i(TAG, "处理停止指令")
                CoroutineScope(Dispatchers.IO).launch {
                    AppOptRunner.stop()
                }
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> Log.w(TAG, "未知指令: ${intent?.action}")
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                "Elaina Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
