@file:Suppress("MatchingDeclarationName")

package com.crescenzi.esptoolbox.presentation.widget

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.crescenzi.esptoolbox.theme.BUTTON_HEIGHT
import com.crescenzi.esptoolbox.theme.CLEAR_BUTTON_HEIGHT
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_S
import com.crescenzi.esptoolbox.theme.SPACE_XS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppButtonType { FILLED, OUTLINED, CLEAR }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppButton(
    txt: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    type: AppButtonType = AppButtonType.FILLED,
    debounceMs: Long = 300L,
    enabled: Boolean = true,
    destructive: Boolean = false,
    fillWidth: Boolean = true,
) {
    require(txt.isNotEmpty()) { "Invalid text passed into AppButton()" }

    val haptic = LocalHapticFeedback.current
    val debouncedTap = rememberDebouncedClick(debounceMs) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onTap()
    }

    val accent = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
    val minHeight = if (type == AppButtonType.CLEAR) CLEAR_BUTTON_HEIGHT else BUTTON_HEIGHT
    val shape = RoundedCornerShape(16.dp)
    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed.value && enabled) 0.992f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "button_bounce",
    )

    val sharedModifier =
        modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .heightIn(min = minHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        pressed.value = true
                        waitForUpOrCancellation()
                        pressed.value = false
                    }
                }
            }

    val contentPadding =
        if (type == AppButtonType.CLEAR) {
            PaddingValues(horizontal = SPACE_S, vertical = 0.dp)
        } else {
            PaddingValues(horizontal = SPACE_L, vertical = SPACE_XS)
        }
    val label: @Composable () -> Unit = {
        Text(
            text = txt,
            style =
                if (type == AppButtonType.CLEAR) {
                    MaterialTheme.typography.bodyMedium
                } else {
                    MaterialTheme.typography.labelLarge
                },
            fontWeight = if (type == AppButtonType.CLEAR) FontWeight.Medium else FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    when (type) {
        AppButtonType.FILLED ->
            Button(
                onClick = debouncedTap,
                enabled = enabled,
                colors =
                    if (destructive) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    },
                shape = shape,
                contentPadding = contentPadding,
                modifier = sharedModifier,
            ) { label() }

        AppButtonType.OUTLINED ->
            OutlinedButton(
                onClick = debouncedTap,
                enabled = enabled,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = accent),
                border =
                    BorderStroke(
                        width = 1.dp,
                        color = accent.copy(alpha = if (enabled) 0.4f else 0.15f),
                    ),
                shape = shape,
                contentPadding = contentPadding,
                modifier = sharedModifier,
            ) { label() }

        AppButtonType.CLEAR ->
            TextButton(
                onClick = debouncedTap,
                enabled = enabled,
                colors = ButtonDefaults.textButtonColors(contentColor = accent),
                shape = shape,
                contentPadding = contentPadding,
                modifier = sharedModifier,
            ) { label() }
    }
}

@Composable
internal fun rememberDebouncedClick(
    debounceMs: Long = 300L,
    onClick: () -> Unit,
): () -> Unit {
    val scope = rememberCoroutineScope()
    var cooling by remember { mutableStateOf(false) }
    return {
        if (!cooling) {
            cooling = true
            onClick()
            scope.launch {
                delay(debounceMs)
                cooling = false
            }
        }
    }
}
