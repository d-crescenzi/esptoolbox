package com.crescenzi.esptoolbox.presentation.main_shell.usb_connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
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
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.NAV_PILL_CLEARANCE
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_M
import com.crescenzi.esptoolbox.theme.SPACE_XL

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
            scrollable = false,
            bottomBar = {
                UsbConnectionActionsWidget(
                    Modifier.padding(bottom = NAV_PILL_CLEARANCE),
                    usbConnectionViewModel,
                ) {
                    usbConnectionViewModel.sendCredentials(
                        UsbConnectionArgs(
                            ssid.value,
                            pwd.value,
                            format.value,
                            baudRate.value
                        ), onReqUsbPermission
                    )
                }
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LATERAL_PADDING)
                    .padding(top = SPACE_XL, bottom = SPACE_L),
                verticalArrangement = Arrangement.spacedBy(SPACE_M)
            ) {

                UsbConnectionCredentialsWidget(ssid, pwd)

                UsbConnectionStatusWidget(deviceSnapshot)

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
            }
        }

        if (loading) {
            LoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
