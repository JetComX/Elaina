package com.jetcomx.elaina.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jetcomx.elaina.ui.component.LiquidBottomTab
import com.jetcomx.elaina.ui.component.LiquidBottomTabs
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun NavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    backdrop: Backdrop? = null,
    windowInsetsPadding: Boolean = true
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val uiStyle by AppSettings.uiStyle.collectAsState()

    val navigate: (NavData) -> Unit = { destination ->
        navController.navigate(destination.route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) { saveState = true }
        }
    }

    if (uiStyle == 1) {
        val navBackdrop = backdrop ?: rememberLayerBackdrop()
        val tabsCount = NavData.entries.size
        val highlightEnabled by AppSettings.glassHighlightEnabled.collectAsState()

        val currentIndex = NavData.entries.indexOfFirst { item ->
            currentDestination?.hierarchy?.any { it.route == item.route } == true
        }.coerceAtLeast(0)

        var tappedIndex by remember { mutableIntStateOf(currentIndex) }
        LaunchedEffect(currentIndex) { tappedIndex = currentIndex }

        LiquidBottomTabs(
            selectedTabIndex = { tappedIndex },
            onTabSelected = { index -> navigate(NavData.entries[index]) },
            backdrop = navBackdrop,
            tabsCount = tabsCount,
            highlightEnabled = highlightEnabled,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            repeat(tabsCount) { index ->
                val item = NavData.entries[index]
                val selected = currentIndex == index
                val color by animateColorAsState(
                    if (selected) MiuixTheme.colorScheme.primary
                    else MiuixTheme.colorScheme.onSurfaceVariantActions,
                    spring(),
                    label = "tabColor"
                )
                val colorFilter = ColorFilter.tint(color)

                LiquidBottomTab(onClick = { tappedIndex = index }) {
                    Box(
                        Modifier
                            .size(25.dp)
                            .paint(
                                rememberVectorPainter(item.icon),
                                colorFilter = colorFilter
                            )
                    )
                    BasicText(
                        text = stringResource(item.label),
                        style = TextStyle(
                            color = color,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    } else {
        NavigationBar(modifier = modifier, showDivider = true) {
            NavData.entries.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                NavigationBarItem(
                    selected = selected,
                    onClick = { if (!selected) navigate(item) },
                    icon = item.icon,
                    label = stringResource(item.label)
                )
            }
        }
    }
}
