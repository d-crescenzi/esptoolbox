package com.crescenzi.esptoolbox.presentation.main_shell.usb_flash

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.core.AppConstants.PICK_MIME_TYPE
import com.crescenzi.esptoolbox.core.getFileNameWithoutBin
import com.crescenzi.esptoolbox.presentation.main_shell.usb_flash.util.UsbUpdaterButtonsWidget
import com.crescenzi.esptoolbox.presentation.util.getMessage
import com.crescenzi.esptoolbox.presentation.widget.AppButton
import com.crescenzi.esptoolbox.presentation.widget.AppButtonType
import com.crescenzi.esptoolbox.presentation.widget.AppScaffold
import com.crescenzi.esptoolbox.presentation.widget.AppTextField
import com.crescenzi.esptoolbox.presentation.widget.UsbBaudRateWidget
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.NAV_PILL_CLEARANCE
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_S

import com.crescenzi.esp32.usb.UsbRepo
import com.crescenzi.esp32.usb.model.LogLevel
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun USBFlashScreen(usbFlashViewModel: USBFlashViewModel) {

    val context = LocalContext.current
    val loading by usbFlashViewModel.loading.collectAsStateWithLifecycle()

    val baudRate by usbFlashViewModel.baudRate.collectAsStateWithLifecycle()
    val flashFiles by usbFlashViewModel.flashFiles.collectAsStateWithLifecycle()

    val usbRepo = koinInject<UsbRepo>()
    val currentDevice by usbRepo._currentDevice.collectAsStateWithLifecycle()

    // == Flash is enabled only with >=1 attached file and valid addresses == //
    val flashEnabled = flashFiles.isNotEmpty() && flashFiles.all { it.addressValid }


    /**
     * File launcher
     */
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            usbFlashViewModel.addFlashFile(
                label = it.getFileNameWithoutBin(context).toString(),
                uri = it
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AppScaffold(
            title = stringResource(R.string.flash_title),
            reserveTopBarSpace = true,
            scrollable = false,
            bottomBar = {
                UsbUpdaterButtonsWidget(
                    modifier = Modifier.padding(bottom = NAV_PILL_CLEARANCE),
                    flashEnabled = flashEnabled && !loading,
                    resetEnabled = currentDevice != null && !loading,
                    onReset = usbFlashViewModel::commandReset,
                    onFlash = {
                        /**
                         * Takes all entries and flashes them
                         */
                        usbFlashViewModel.flash(context)
                    }
                )
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LATERAL_PADDING)
                    .padding(top = CONTENT_TOP_PADDING, bottom = NAV_PILL_CLEARANCE + SPACE_L),
                verticalArrangement = Arrangement.spacedBy(SPACE_L)
            ) {
                UsbBaudRateWidget(
                    selectedBaudRate = baudRate,
                    onBaudRateSelected = {
                        usbFlashViewModel.updateBaudRate(it)
                    }
                )

                AppButton(
                    txt = stringResource(R.string.btn_add_file),
                    type = AppButtonType.CLEAR,
                    enabled = !loading && flashFiles.size < USBFlashViewModel.MAX_FLASH_FILES,
                    onTap = {
                        filePicker.launch(arrayOf(PICK_MIME_TYPE))
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SPACE_S)
                ) {
                    if (flashFiles.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_flash_files),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        flashFiles.forEachIndexed { index, fileEntry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .heightIn(min = 56.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                val removeInteractionSource = remember { MutableInteractionSource() }

                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.secondary, shape = CircleShape)
                                        .clickable(
                                            interactionSource = removeInteractionSource,
                                            indication = null
                                        ) {
                                            usbFlashViewModel.removeFlashFile(index)
                                        }
                                        .padding(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Text(
                                    text = fileEntry.label,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .padding(horizontal = SPACE_L)
                                )
                                Box(modifier = Modifier.weight(1f)) {
                                    key(fileEntry.uri) {
                                        AppTextField(
                                            opt = KeyboardOptions.Default,
                                            label = "",
                                            initialValue = "0x${fileEntry.address.toString(16)}",
                                            onValueChange = { newText ->
                                                try {
                                                    val parsed = newText
                                                        .trim()
                                                        .lowercase()
                                                        .removePrefix("0x")
                                                        .toIntOrNull(16)

                                                    usbFlashViewModel.updateFlashAddress(
                                                        index = index,
                                                        address = parsed,
                                                        addressValid = parsed != null
                                                    )
                                                } catch (e: Exception) {
                                                    usbFlashViewModel.logRepo.plusLog(
                                                        getMessage(context, e),
                                                        LogLevel.ERROR
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        if (loading) {
            LoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
