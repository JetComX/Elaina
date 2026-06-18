package com.jetcomx.elaina.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jetcomx.elaina.R
import com.jetcomx.elaina.navigation.MainNavScreen
import com.jetcomx.elaina.navigation.LocalNavigator
import com.jetcomx.elaina.navigation.Route
import com.jetcomx.elaina.navigation.rememberNavigator
import com.jetcomx.elaina.screen.settings.AboutScreen
import com.jetcomx.elaina.screen.settings.CreditsScreen
import com.jetcomx.elaina.screen.settings.LiquidGlassCustomizeScreen
import com.jetcomx.elaina.screen.welcome.WelcomeScreen
import com.jetcomx.elaina.ui.component.DreamFluidBackground
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.ui.component.ShaderWarmUp
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LiquidInputTextField
import com.jetcomx.elaina.ui.component.LiquidModalBottomSheet
import com.jetcomx.elaina.utils.AppSettings
import com.jetcomx.elaina.viewmodel.MainViewModel
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowBottomSheet
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = viewModel()
    val showWelcome by viewModel.showWelcome.collectAsState()
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val bgPath by AppSettings.backgroundImagePath.collectAsState()
    val verified by AppSettings.verified.collectAsState()

    val bgUri = if (backgroundStyle == 2 && !bgPath.isNullOrEmpty()) {
        try { Uri.parse(bgPath) } catch (_: Exception) { null }
    } else null

    val bgBackdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {
        ShaderWarmUp()

        if (bgUri != null) {
            Box(modifier = Modifier.fillMaxSize().layerBackdrop(bgBackdrop)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MiuixTheme.colorScheme.background)
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bgUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else if (backgroundStyle == 1) {
            DreamFluidBackground()
        } else if (uiStyle == 1) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(bgBackdrop)
                    .background(MiuixTheme.colorScheme.background)
            )
        }

        CompositionLocalProvider(LocalBackgroundBackdrop provides bgBackdrop) {
            if (showWelcome) {
                WelcomeScreen { viewModel.dismissWelcome() }
            } else {
                val navigator = rememberNavigator(Route.Main)
                val entryDecorator = rememberSaveableStateHolderNavEntryDecorator<Route>()

                CompositionLocalProvider(LocalNavigator provides navigator) {
                    @Suppress("UNCHECKED_CAST")
                    NavDisplay(
                        backStack = navigator.backStack as List<Route>,
                        entryDecorators = listOf(entryDecorator),
                        onBack = { navigator.pop() },
                        entryProvider = entryProvider {
                            entry<Route.Main> {
                                MainNavScreen()
                            }
                            entry<Route.About> {
                                AboutScreen(onBack = { navigator.pop() })
                            }
                            entry<Route.Credits> {
                                CreditsScreen(onBack = { navigator.pop() })
                            }
                            entry<Route.LiquidGlassCustomize> {
                                LiquidGlassCustomizeScreen()
                            }
                        }
                    )
                }

                if (!verified) {
                    VerificationDialog(backgroundBackdrop = bgBackdrop)
                }
            }
        }
    }
}

@Composable
private fun VerificationDialog(
    backgroundBackdrop: com.kyant.backdrop.Backdrop,
) {
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val aidsfgdifbsu = listOf("酷安", "Gitee", "JetComX")
    var text by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    fun validateAndSubmit() {
        val trimmed = text.trim()
        if (aidsfgdifbsu.any { it.equals(trimmed, ignoreCase = true) }) {
            AppSettings.setVerified(true)
        } else {
            showError = true
        }
    }

    if (uiStyle == 1) {
        LiquidModalBottomSheet(
            show = true,
            backdrop = backgroundBackdrop,
            onDismissRequest = {},
            enableDismiss = false,
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
                Text(
                    stringResource(R.string.verify_title),
                    fontSize = 20.sp,
                    color = MiuixTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                Text(
                    stringResource(R.string.verify_description),
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                LiquidInputTextField(
                    text = text,
                    onChange = { text = it; showError = false },
                    label = stringResource(R.string.verify_hint),
                    backdrop = backgroundBackdrop,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
                if (showError) {
                    Text(
                        stringResource(R.string.verify_failed),
                        color = MiuixTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                LiquidButton(
                    backdrop = backgroundBackdrop,
                    onClick = { validateAndSubmit() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        stringResource(R.string.verify_button),
                        color = MiuixTheme.colorScheme.primary
                    )
                }
            }
        }
    } else {
        WindowBottomSheet(
            show = true,
            title = stringResource(R.string.verify_title),
            onDismissRequest = {},
            allowDismiss = false,
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    stringResource(R.string.verify_description),
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                TextField(
                    value = text,
                    onValueChange = { text = it; showError = false },
                    label = stringResource(R.string.verify_hint),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        stringResource(R.string.verify_failed),
                        color = MiuixTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { validateAndSubmit() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MiuixTheme.colorScheme.primary,
                        contentColor = MiuixTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.verify_button))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
