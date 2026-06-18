package com.jetcomx.elaina.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetcomx.elaina.screen.home.HomeScreen
import com.jetcomx.elaina.screen.log.LogScreen
import com.jetcomx.elaina.screen.settings.SettingScreen
import com.jetcomx.elaina.screen.thread.ThreadScreen
import com.jetcomx.elaina.ui.component.LocalIsLiquidGlass
import com.jetcomx.elaina.ui.component.LocalLiquidGlassBackdrop
import com.jetcomx.elaina.ui.component.LocalLiquidGlassOnSurface
import com.jetcomx.elaina.ui.component.LocalLiquidGlassSurface
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MainNavScreen() {
    val navController = rememberNavController()
    val uiStyle by AppSettings.uiStyle.collectAsState()

    if (uiStyle == 1) {
        LiquidGlassMainScreen(navController = navController)
    } else {
        MiuiXMainScreen(navController = navController)
    }
}

@Composable
private fun MiuiXMainScreen(navController: NavHostController) {
    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background,
            bottomBar = { NavBar(navController = navController, windowInsetsPadding = false) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = NavData.Home.route,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                composable(NavData.Home.route) {
                    HomeScreen(onNavigateToThread = {
                        navController.navigate(NavData.Thread.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    })
                }
                composable(NavData.Thread.route) { ThreadScreen() }
                composable(NavData.Log.route) { LogScreen() }
                composable(NavData.Setting.route) { SettingScreen() }
            }
        }
    }
}

@Composable
private fun LiquidGlassMainScreen(navController: NavHostController) {
    val backdrop = rememberLayerBackdrop()
    val isLight = !androidx.compose.foundation.isSystemInDarkTheme()

    val adjustedOnSurface = if (isLight) Color(0xFF1A1A1A) else Color(0xFFEEEEEE)
    val adjustedSurface = if (isLight) Color(0xFFF5F5F5) else Color(0xFF1E1E1E)

    CompositionLocalProvider(
        LocalIsLiquidGlass provides true,
        LocalLiquidGlassBackdrop provides backdrop,
        LocalLiquidGlassOnSurface provides adjustedOnSurface,
        LocalLiquidGlassSurface provides adjustedSurface,
    ) {
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = NavData.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop)
            ) {
                composable(NavData.Home.route) {
                    HomeScreen(onNavigateToThread = {
                        navController.navigate(NavData.Thread.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    })
                }
                composable(NavData.Thread.route) { ThreadScreen() }
                composable(NavData.Log.route) { LogScreen() }
                composable(NavData.Setting.route) { SettingScreen() }
            }

            NavBar(
                navController = navController,
                backdrop = backdrop,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
