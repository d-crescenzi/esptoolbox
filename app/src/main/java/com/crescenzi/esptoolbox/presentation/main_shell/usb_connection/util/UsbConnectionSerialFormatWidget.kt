package com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.util

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
import com.crescenzi.esptoolbox.presentation.widget.EditText
import com.crescenzi.esptoolbox.theme.CARD_RADIUS
import com.crescenzi.esptoolbox.theme.FIELD_RADIUS
import com.crescenzi.esp32.params.SerialFormat


/**
 * Manages serial format settings — custom dropdown anchored to our EditText field
 */
@Composable
fun UsbConnectionSerialFormatWidget(
    modifier: Modifier = Modifier,
    selectedFormat: SerialFormat,
    onFormatSelected: (SerialFormat) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val serialFormats = listOf(
        SerialFormat.Plain,
        SerialFormat.Json,
        SerialFormat.AT,
        SerialFormat.Csv,
        SerialFormat.Credentials,
        SerialFormat.Custom
    )

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
            key(selectedFormat) {
                EditText(
                    modifier = Modifier.fillMaxWidth(),
                    opt = KeyboardOptions.Default,
                    onValueChange = {},
                    label = "Format",
                    initialValue = selectedFormat.compactDescription(),
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
            serialFormats.forEach { format ->
                DropdownMenuItem(
                    text = {
                        Text(
                            format.description,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    onClick = {
                        onFormatSelected(format)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun SerialFormat.compactDescription(): String =
    when (this) {
        SerialFormat.Plain -> "Plain"
        SerialFormat.Json -> "JSON"
        SerialFormat.AT -> "AT"
        SerialFormat.Csv -> "CSV"
        SerialFormat.Credentials -> "Credentials"
        SerialFormat.Custom -> "Custom"
    }
