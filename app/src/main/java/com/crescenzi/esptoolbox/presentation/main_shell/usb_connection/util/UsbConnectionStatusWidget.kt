package com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.crescenzi.esptoolbox.R
import com.crescenzi.esp32.usb.model.UsbStatus.SnapshotUsb
import com.crescenzi.esptoolbox.theme.SPACE_XS

/**
 * Shows some of the board characteristics via USB
 */
@Composable
fun UsbConnectionStatusWidget(deviceSnapshot: SnapshotUsb) {
    val labels = listOf(
        stringResource(R.string.usb_version) to deviceSnapshot.version,
        stringResource(R.string.usb_manufacturer) to deviceSnapshot.manufacturerName,
        stringResource(R.string.usb_vendor_id) to deviceSnapshot.vendorId.toString(),
        stringResource(R.string.usb_product_id) to deviceSnapshot.productId.toString(),
        stringResource(R.string.usb_port) to deviceSnapshot.port.toString()
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        labels.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SPACE_XS),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
