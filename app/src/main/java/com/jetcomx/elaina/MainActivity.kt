package com.jetcomx.elaina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jetcomx.elaina.screen.loading.LoadingScreen
import com.jetcomx.elaina.theme.ElainaTheme
import com.jetcomx.elaina.utils.CrashHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrashHandler.init()
        enableEdgeToEdge()
        setContent {
            ElainaTheme {
                LoadingScreen()
            }
        }
    }
}
