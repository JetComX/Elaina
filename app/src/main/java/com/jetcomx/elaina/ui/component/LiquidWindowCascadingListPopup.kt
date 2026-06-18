package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import top.yukonga.miuix.kmp.basic.DropdownColors
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowCascadingListPopup

@Composable
fun LiquidWindowCascadingListPopup(
    show: Boolean,
    entries: List<DropdownEntry>,
    onDismissRequest: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    glassEnabled: Boolean = false,
    popupModifier: Modifier = Modifier,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.End,
    enableWindowDim: Boolean = true,
    onDismissFinished: (() -> Unit)? = null,
    maxHeight: Dp? = null,
    minWidth: Dp = 200.dp,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(
        selectedContentColor = MiuixTheme.colorScheme.primary,
        selectedIndicatorColor = MiuixTheme.colorScheme.primary,
    ),
    collapseOnSelection: Boolean = true,
) {
    val glassModifier = if (glassEnabled) {
        Modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(20.dp) },
                effects = { standardGlassEffects() }
            )
            .padding(12.dp)
    } else {
        Modifier
    }

    WindowCascadingListPopup(
        show = show,
        entries = entries,
        onDismissRequest = onDismissRequest,
        popupModifier = popupModifier.then(glassModifier),
        popupPositionProvider = popupPositionProvider,
        alignment = alignment,
        enableWindowDim = enableWindowDim,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        minWidth = minWidth,
        dropdownColors = dropdownColors,
        collapseOnSelection = collapseOnSelection,
    )
}
