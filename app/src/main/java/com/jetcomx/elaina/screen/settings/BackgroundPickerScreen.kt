package com.jetcomx.elaina.screen.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jetcomx.elaina.R
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BackgroundPickerScreen(onBack: () -> Unit, onConfirmed: (() -> Unit)? = null) {
    val primary = MiuixTheme.colorScheme.primary
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val savedPath by AppSettings.backgroundImagePath.collectAsState()
    val hasBg = !savedPath.isNullOrEmpty()
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedUri = uri
        }
    }

    val displayUri = selectedUri ?: savedPath?.let {
        try {
            Uri.parse(it)
        } catch (_: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (displayUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(displayUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = stringResource(R.string.settings_choose_bg_desc),
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantActions
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (uiStyle == 1) {
                LiquidButton(
                    backdrop = backdrop,
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_choose_bg), color = primary)
                }
            } else {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_choose_bg), color = primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (displayUri != null) {
                if (uiStyle == 1) {
                    LiquidButton(
                        backdrop = backdrop,
                        onClick = {
                            selectedUri?.let {
                                AppSettings.setBackgroundImagePath(it.toString())
                                AppSettings.setBackgroundStyle(2)
                                onConfirmed?.invoke()
                            }
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.bg_picker_confirm), color = primary)
                    }
                } else {
                    Button(
                        onClick = {
                            selectedUri?.let {
                                AppSettings.setBackgroundImagePath(it.toString())
                                AppSettings.setBackgroundStyle(2)
                                onConfirmed?.invoke()
                            }
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.bg_picker_confirm), color = primary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (hasBg) {
                if (uiStyle == 1) {
                    LiquidButton(
                        backdrop = backdrop,
                        onClick = {
                            AppSettings.setBackgroundImagePath(null)
                            AppSettings.setBackgroundStyle(0)
                            selectedUri = null
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.bg_picker_reset), color = primary)
                    }
                } else {
                    Button(
                        onClick = {
                            AppSettings.setBackgroundImagePath(null)
                            AppSettings.setBackgroundStyle(0)
                            selectedUri = null
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.bg_picker_reset), color = primary)
                    }
                }
            }

        }
    }
}
