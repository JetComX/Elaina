package com.jetcomx.elaina.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.graphics.vector.ImageVector
import com.jetcomx.elaina.R

enum class NavData(
    val route: String,
    val label: Int,
    val icon: ImageVector
) {
    Home(
        route = "home",
        label = R.string.nav_home,
        icon = Icons.Filled.Home
    ),
    Thread(
        route = "thread",
        label = R.string.nav_thread,
        icon = Icons.Filled.AutoAwesome
    ),
    Log(
        route = "log",
        label = R.string.nav_log,
        icon = Icons.Filled.Terminal
    ),
    Setting(
        route = "setting",
        label = R.string.nav_setting,
        icon = Icons.Filled.Settings
    )
}