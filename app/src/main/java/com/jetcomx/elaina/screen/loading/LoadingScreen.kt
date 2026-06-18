package com.jetcomx.elaina.screen.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jetcomx.elaina.R
import com.jetcomx.elaina.screen.MainScreen
import com.jetcomx.elaina.ui.component.LiquidCircularProgressIndicator
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppSettings
import com.jetcomx.elaina.viewmodel.LoadingViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun LoadingScreen() {
    val viewModel: LoadingViewModel = viewModel()
    val progress by viewModel.progress.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val loadingComplete by viewModel.loadingComplete.collectAsState()
    val hasRoot by viewModel.hasRoot.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    when {
        loadingComplete -> MainScreen()
        hasRoot == false -> LoadingFailedContent(
            message = errorMessage ?: stringResource(R.string.no_root_permission),
            onRetry = viewModel::retry
        )

        else -> LoadingProgressContent(
            statusText = statusText
        )
    }
}

@Composable
private fun LoadingProgressContent(statusText: String) {
    val primary = MiuixTheme.colorScheme.primary
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.by_jetcomx),
                fontSize = 14.sp,
                color = primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiStyle == 1) {
                LiquidCircularProgressIndicator(backdrop = backdrop)
            } else {
                InfiniteProgressIndicator(color = primary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = statusText,
                fontSize = 13.sp,
                color = primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingFailedContent(message: String, onRetry: () -> Unit) {

    val error = MiuixTheme.colorScheme.error

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {

            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.root_permission_required),
                style = MaterialTheme.typography.bodyLarge,
                color = MiuixTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = error
                )
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    color = MiuixTheme.colorScheme.onError
                )
            }
        }
    }
}
