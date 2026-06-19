package com.jetcomx.elaina.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

class Navigator(val backStack: SnapshotStateList<NavKey>) {
    fun push(route: Route) {
        backStack.add(route)
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun current(): Route = backStack.last() as Route

    fun backStackSize(): Int = backStack.size
}

@Composable
fun rememberNavigator(initialRoute: Route = Route.Main): Navigator {
    val backStack = remember { mutableStateListOf<NavKey>(initialRoute) }
    return remember(backStack) { Navigator(backStack) }
}
