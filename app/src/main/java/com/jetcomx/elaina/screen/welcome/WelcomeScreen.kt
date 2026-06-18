package com.jetcomx.elaina.screen.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetcomx.elaina.R
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    var isExiting by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val primary = MiuixTheme.colorScheme.primary
    val onPrimary = MiuixTheme.colorScheme.onPrimary

    val animatedSize by animateDpAsState(
        targetValue = if (expanded) 65.dp else 60.dp,
        label = "fabSize"
    )

    val pageTexts = listOf(
        stringResource(R.string.hi),
        stringResource(R.string.welcome_description),
        stringResource(R.string.welcome_continue),
    )

    Box(modifier = Modifier.fillMaxSize().background(MiuixTheme.colorScheme.background)) {
        AnimatedVisibility(
            visible = !isExiting,
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }
                ) { page ->
                    Text(
                        text = pageTexts[page],
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !isExiting,
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    expanded = !expanded
                    if (currentPage < 2) {
                        currentPage++
                    } else {
                        isExiting = true
                    }
                },
                containerColor = primary,
                minWidth = animatedSize,
                minHeight = animatedSize
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.ArrowRight,
                    contentDescription = null,
                    tint = onPrimary
                )
            }
        }
    }

    if (isExiting) {
        LaunchedEffect(Unit) {
            delay(300)
            onContinue()
        }
    }
}
