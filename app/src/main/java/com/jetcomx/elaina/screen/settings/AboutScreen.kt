package com.jetcomx.elaina.screen.settings

import android.R.attr.fontWeight
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetcomx.elaina.R
import com.jetcomx.elaina.ui.component.LiquidButton
import com.jetcomx.elaina.ui.component.LocalBackgroundBackdrop
import com.jetcomx.elaina.utils.AppSettings
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val URL_GITHUB = "https://github.com/JetComX"
private const val URL_GITEE = "https://gitee.com/jetcomx"
private const val URL_COOLAPK = "https://www.coolapk.com/u/41212343?from=qr"
private const val URL_QQ_GROUP = "https://qun.qq.com/universal-share/share?ac=1&authKey=YMePtQ6n%2FCz5lMWWyuYQPziXmlMtZhg%2FRo%2Flu9dkmEPRrmObR%2Bv32OkOSLIH6uEw&busi_data=eyJncm91cENvZGUiOiIxMDkzMjcyMjgyIiwidG9rZW4iOiJKTm9RNzdLd2M5WXV1WXllSmdSQkpwU3Q1SFhZRzkvNWpac0UrQkFocFc0Z080UHN5SERmT0VTUytQMWg1QXlUIiwidWluIjoiMzk4MTU4NTk5MyJ9&data=SnPSPMftyWLJTzTE-Aod0kvSwYoX7Q1Td8QSbfFx_HIZ5mi8ubknMcCPk9nZ6yaqpva3j0mmxVO2a_RJxOaQow&svctype=4&tempid=h5_group_info"

@Composable
fun AboutScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val backgroundStyle by AppSettings.backgroundStyle.collectAsState()
    val customBg = backgroundStyle != 0
    val contentColor = MiuixTheme.colorScheme.onSurface
    val summaryColor = MiuixTheme.colorScheme.onSurfaceSecondary
    val uiStyle by AppSettings.uiStyle.collectAsState()
    val backdrop = LocalBackgroundBackdrop.current ?: rememberLayerBackdrop()

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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.about_developer),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(R.drawable.developer_jetcomx),
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "JetComX",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.motto),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiStyle == 1) {
                    LiquidButton(
                        backdrop = backdrop,
                        onClick = { openUrl(URL_GITHUB) },
                        modifier = Modifier.fillMaxWidth(),
                        content = { Text(stringResource(R.string.about_btn_github)) }
                    )
                    LiquidButton(
                        backdrop = backdrop,
                        onClick = { openUrl(URL_GITEE) },
                        modifier = Modifier.fillMaxWidth(),
                        content = { Text(stringResource(R.string.about_btn_gitee)) }
                    )
                    LiquidButton(
                        backdrop = backdrop,
                        onClick = { openUrl(URL_COOLAPK) },
                        modifier = Modifier.fillMaxWidth(),
                        content = { Text(stringResource(R.string.about_btn_coolapk)) }
                    )
                    LiquidButton(
                        backdrop = backdrop,
                        onClick = { openUrl(URL_QQ_GROUP) },
                        modifier = Modifier.fillMaxWidth(),
                        content = { Text(stringResource(R.string.about_btn_qq_group)) }
                    )
                } else {
                    Button(
                        onClick = { openUrl(URL_GITHUB) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.about_btn_github))
                    }
                    Button(
                        onClick = { openUrl(URL_GITEE) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.about_btn_gitee))
                    }
                    Button(
                        onClick = { openUrl(URL_COOLAPK) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.about_btn_coolapk))
                    }
                    Button(
                        onClick = { openUrl(URL_QQ_GROUP) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.about_btn_qq_group))
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.welcome_description),
                fontSize = 14.sp,
                color = summaryColor,
                lineHeight = 22.sp
            )
        }
    }
}
