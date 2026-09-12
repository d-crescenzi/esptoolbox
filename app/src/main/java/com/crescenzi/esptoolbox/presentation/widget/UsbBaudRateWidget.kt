package com.crescenzi.esptoolbox.presentation.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.crescenzi.esptoolbox.theme.CARD_RADIUS
import com.crescenzi.esptoolbox.theme.FIELD_RADIUS
import com.crescenzi.esp32.params.BaudRateFormat


/*
Manages the baud rate with a custom dropdown anchored to AppTextField.
 */
@Composable
fun UsbBaudRateWidget(
    modifier: Modifier = Modifier,
    selectedBaudRate: BaudRateFormat,
    onBaudRateSelected: (BaudRateFormat) -> Unit,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow_rotation"
    )

    Box(modifier = modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(FIELD_RADIUS))
                .clickable {
                    expanded = true
                }
        ) {
            key(selectedBaudRate) {
                AppTextField(
                    modifier = Modifier.fillMaxWidth(),
                    opt = KeyboardOptions.Default,
                    onValueChange = {},
                    label = "Baud Rate",
                    initialValue = "${selectedBaudRate.value}",
                    readOnly = true
                )
            }
            Text(
                text = "▼",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 26.dp)
                    .rotate(arrowRotation)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(CARD_RADIUS)
        ) {
            BaudRateFormat.entries.forEach { rate ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${rate.value} bps",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    onClick = {
                        onBaudRateSelected(rate)
                        expanded = false
                    }
                )
            }
        }
    }
}
