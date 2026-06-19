package com.jetcomx.elaina.myconst

import android.content.Context

fun getVersionName(context: Context): String = runCatching {
    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.2"
}.getOrDefault("1.2")
