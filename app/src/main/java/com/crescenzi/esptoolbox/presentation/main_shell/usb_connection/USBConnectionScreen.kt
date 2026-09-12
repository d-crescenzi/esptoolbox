package com.crescenzi.esptoolbox.presentation.main_shell.usb_connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crescenzi.esptoolbox.R
import com.crescenzi.esp32.params.BaudRateFormat
import com.crescenzi.esp32.params.SerialFormat
import com.crescenzi.esp32.usb.model.UsbConnectionArgs
import com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.util.UsbConnectionActionsWidget
import com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.util.UsbConnectionCredentialsWidget
import com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.util.UsbConnectionSerialFormatWidget
import com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.util.UsbConnectionStatusWidget
import com.crescenzi.esptoolbox.presentation.widget.AppScaffold
import com.crescenzi.esptoolbox.presentation.widget.UsbBaudRateWidget
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.NAV_PILL_CLEARANCE
import com.crescenzi.esptoolbox.theme.SPACE_L

/**
 * There will always be only one device connected via USB
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun USBConnectionScreen(
    usbConnectionViewModel: USBConnectionViewModel,
    onReqUsbPermission: () -> Unit
) {
    val context = LocalContext.current
    val loading by usbConnectionViewModel.loading.collectAsStateWithLifecycle()

    /**
     * Only at startup
     */
    LaunchedEffect(Unit) {
        usbConnectionViewModel.openObserver(context)
    }


    val deviceSnapshot by (usbConnectionViewModel.currentDeviceSnapshot.collectAsStateWithLifecycle())

    val ssid = rememberSaveable { mutableStateOf(usbConnectionViewModel.ssidState.value) }
    val pwd = rememberSaveable { mutableStateOf("") }
    val baudRate = remember { mutableStateOf(BaudRateFormat.B115200) }
    val format = remember { mutableStateOf<SerialFormat>(SerialFormat.Plain) }

    Box(modifier = Modifier.fillMaxSize()) {

        AppScaffold(
            title = stringResource(R.string.usb_title),
            reserveTopBarSpace = true,
            contentPadding = PaddingValues(
                start = LATERAL_PADDING,
                end = LATERAL_PADDING,
                top = CONTENT_TOP_PADDING,
                bottom = NAV_PILL_CLEARANCE
            )
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(SPACE_L)) {

                Text(
                    text = stringResource(R.string.board_name),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                UsbConnectionCredentialsWidget(ssid, pwd)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SPACE_L)
                ) {
                    UsbBaudRateWidget(
                        modifier = Modifier.weight(1f),
                        selectedBaudRate = baudRate.value,
                        onBaudRateSelected = { baudRate.value = it }
                    )

                    UsbConnectionSerialFormatWidget(
                        modifier = Modifier.weight(1f),
                        selectedFormat = format.value,
                        onFormatSelected = { format.value = it }
                    )
                }


                UsbConnectionActionsWidget(
                    Modifier,
                    usbConnectionViewModel,
                ) {
                    /**
                     * - Button onClick  -->  Send credentials or request permissions
                     */
                    usbConnectionViewModel.sendCredentials(
                        UsbConnectionArgs(
                            ssid.value,
                            pwd.value,
                            format.value,
                            baudRate.value
                        ), onReqUsbPermission
                    )
                }

                HorizontalDivider()

                UsbConnectionStatusWidget(deviceSnapshot)

                Text(
                    text = stringResource(R.string.usb_connection_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (loading) {
            LoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
