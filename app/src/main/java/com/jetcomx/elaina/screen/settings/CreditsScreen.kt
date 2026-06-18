package com.jetcomx.elaina.screen.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetcomx.elaina.R
import com.jetcomx.elaina.ui.component.LiquidCard
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType

private data class OssProject(
    val name: String,
    val license: String,
    val licenseUrl: String,
    val projectUrl: String
)

private data class SpecialThanks(
    val name: String,
    val author: String,
    val desc: String,
    val url: String
)

private val ossProjects = listOf(
    OssProject(
        name = "Jetpack Compose",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://developer.android.com/jetpack/compose"
    ),
    OssProject(
        name = "Miuix KMP",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://github.com/YuKongA/MiuixKMP"
    ),
    OssProject(
        name = "Kyant Backdrop",
        license = "MIT",
        licenseUrl = "https://opensource.org/licenses/MIT",
        projectUrl = "https://github.com/kyant0/backdrop"
    ),
    OssProject(
        name = "Kyant Shapes",
        license = "MIT",
        licenseUrl = "https://opensource.org/licenses/MIT",
        projectUrl = "https://github.com/kyant0/shapes"
    ),
    OssProject(
        name = "Kotlin Coroutines",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://github.com/Kotlin/kotlinx.coroutines"
    ),
    OssProject(
        name = "Vico",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://github.com/patrykandpatrick/vico"
    ),
    OssProject(
        name = "Coil",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://github.com/coil-kt/coil"
    ),
    OssProject(
        name = "AndroidX Navigation",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://developer.android.com/guide/navigation"
    ),
    OssProject(
        name = "DataStore",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://developer.android.com/topic/libraries/architecture/datastore"
    ),
    OssProject(
        name = "Material Icons Extended",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary"
    ),
    OssProject(
        name = "AndroidX Lifecycle",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://developer.android.com/jetpack/androidx/releases/lifecycle"
    ),
    OssProject(
        name = "NavigationEvent Compose",
        license = "Apache 2.0",
        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
        projectUrl = "https://developer.android.com/jetpack/androidx/releases/navigationevent"
    )
)

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val cardFeedback by AppSettings.cardFeedback.collectAsState()
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()
    val pressFeedback = when (cardFeedback) {
        0 -> PressFeedbackType.None
        1 -> PressFeedbackType.Sink
        else -> PressFeedbackType.Tilt
    }
    val contentColor = MiuixTheme.colorScheme.onSurface
    val summaryColor = MiuixTheme.colorScheme.onSurfaceSecondary
    val primary = MiuixTheme.colorScheme.primary

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    Scaffold(
        containerColor = if (customBg) Color.Transparent else MiuixTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.credits_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            val thanks = SpecialThanks(
                name = stringResource(R.string.credits_special_thanks_name),
                author = stringResource(R.string.credits_special_thanks_author),
                desc = stringResource(R.string.credits_special_thanks_desc),
                url = "http://appopt.suto.top/"
            )
            val thanksTitle = stringResource(R.string.credits_special_thanks)
            if (uiStyle == 1) {
                LiquidCard(
                    onClick = { openUrl(thanks.url) },
                    backdrop = backdrop,
                    pressFeedbackType = pressFeedback,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.credits_special_thanks),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = thanks.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
                            Text(text = "@${thanks.author}", fontSize = 13.sp, color = primary)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(text = thanks.desc, fontSize = 13.sp, color = summaryColor)
                    }
                }
            } else {
                Card(
                    onClick = { openUrl(thanks.url) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainerHighest),
                    pressFeedbackType = pressFeedback
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.credits_special_thanks),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = thanks.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
                            Text(text = "@${thanks.author}", fontSize = 13.sp, color = primary)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(text = thanks.desc, fontSize = 13.sp, color = summaryColor)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ossProjects.forEach { project ->
                    if (uiStyle == 1) {
                        LiquidCard(
                            onClick = { openUrl(project.projectUrl) },
                            backdrop = backdrop,
                            pressFeedbackType = pressFeedback,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = project.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = contentColor
                                    )
                                    Text(
                                        text = project.license,
                                        fontSize = 13.sp,
                                        color = primary,
                                        modifier = Modifier.clickable { openUrl(project.licenseUrl) }
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            onClick = { openUrl(project.projectUrl) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.defaultColors(
                                color = MiuixTheme.colorScheme.surfaceContainerHighest
                            ),
                            pressFeedbackType = pressFeedback
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = project.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = contentColor
                                    )
                                    Text(
                                        text = project.license,
                                        fontSize = 13.sp,
                                        color = primary,
                                        modifier = Modifier.clickable { openUrl(project.licenseUrl) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
