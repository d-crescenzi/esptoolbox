package com.crescenzi.esptoolbox.presentation.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crescenzi.esptoolbox.theme.FIELD_RADIUS
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_S

@Composable
internal fun AppTextField(
    modifier: Modifier = Modifier,
    vt: VisualTransformation = VisualTransformation.None,
    opt: KeyboardOptions,
    actions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
    label: String,
    initialValue: String = "",
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isPassword: Boolean = false
) {
    // == readOnly: non-editable but NOT dimmed (e.g. dropdown anchors). disabled: dimmed == //
    val fieldEnabled = enabled && !readOnly
    val dimmed = !enabled
    var text by rememberSaveable { mutableStateOf(initialValue) }
    var revealed by rememberSaveable { mutableStateOf(false) }
    val effectiveTransformation = when {
        !isPassword -> vt
        revealed -> VisualTransformation.None
        else -> PasswordVisualTransformation()
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colorScheme = MaterialTheme.colorScheme
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    // == Scroll into view when focused == //
    LaunchedEffect(isFocused) {
        if (isFocused) {
            bringIntoViewRequester.bringIntoView()
        }
    }

    val ringColor by animateColorAsState(
        targetValue = if (isFocused && fieldEnabled) colorScheme.primary else Color.Transparent,
        animationSpec = tween(
            durationMillis = 200,
            easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
        ),
        label = "ring_color"
    )

    val containerColor = if (dimmed) {
        colorScheme.surfaceContainer.copy(alpha = 0.5f)
    } else {
        colorScheme.surfaceContainer
    }

    val textColor = if (dimmed) colorScheme.onSurface.copy(alpha = 0.4f) else colorScheme.onSurface
    val placeholderColor = if (dimmed) colorScheme.onSurface.copy(alpha = 0.4f) else colorScheme.onSurfaceVariant
    val cornerRadiusPx = FIELD_RADIUS.dpToPx()
    val ringWidthPx = 2.dp.dpToPx()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .drawBehind {
                // == Draw filled container background == //
                drawRoundRect(
                    color = containerColor,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
                drawRoundRect(
                    color = ringColor,
                    style = Stroke(width = ringWidthPx),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                )
            }
            .padding(horizontal = SPACE_L, vertical = SPACE_L)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    onValueChange(it)
                },
                enabled = fieldEnabled,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = textColor
                ),
                cursorBrush = SolidColor(colorScheme.primary),
                keyboardOptions = opt,
                keyboardActions = actions,
                singleLine = singleLine,
                visualTransformation = effectiveTransformation,
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = placeholderColor
                        )
                    }
                    innerTextField()
                }
            )
            if (isPassword) {
                Icon(
                    imageVector = if (revealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                    contentDescription = null,
                    tint = placeholderColor,
                    modifier = Modifier
                        .padding(start = SPACE_S)
                        .size(22.dp)
                        .clickable(
                            enabled = fieldEnabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { revealed = !revealed }
                )
            }
        }
    }
}

@Composable
private fun Dp.dpToPx(): Float = with(LocalDensity.current) { this@dpToPx.toPx() }
