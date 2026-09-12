package com.crescenzi.esptoolbox.presentation.main_shell.usb_flash

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.crescenzi.esptoolbox.presentation.widget.AppScaffold
import com.crescenzi.esptoolbox.presentation.widget.AppTextField
import com.crescenzi.esptoolbox.presentation.widget.UsbBaudRateWidget
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.NAV_PILL_CLEARANCE
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_S

import com.crescenzi.esp32.usb.UsbRepo
import com.crescenzi.esp32.usb.UsbPermission
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
    val usbPermission by usbRepo._usbPermission.collectAsStateWithLifecycle()
    val usbReady = currentDevice != null && usbPermission == UsbPermission.GRANTED

    // == Flash is enabled only with a ready USB device, >=1 file and valid addresses == //
    val flashEnabled = usbReady && flashFiles.isNotEmpty() && flashFiles.all { it.addressValid } && !loading
    val listEditable = !loading


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
                    flashEnabled = flashEnabled,
                    resetEnabled = usbReady && !loading,
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

                AddFileButton(
                    txt = stringResource(R.string.btn_add_file),
                    enabled = listEditable && flashFiles.size < USBFlashViewModel.MAX_FLASH_FILES,
                    onTap = {
                        filePicker.launch(arrayOf(PICK_MIME_TYPE))
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SPACE_S, Alignment.Top)
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

                                RemoveFileButton(
                                    enabled = listEditable,
                                    onTap = { usbFlashViewModel.removeFlashFile(index) }
                                )

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
                                            enabled = listEditable,
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

@Composable
private fun AddFileButton(
    txt: String,
    enabled: Boolean,
    onTap: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "add_file_bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 36.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        pressed = true
                        val up = waitForUpOrCancellation()
                        pressed = false
                        if (up != null) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTap()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = txt,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.35f)
        )
    }
}

@Composable
private fun RemoveFileButton(
    enabled: Boolean,
    onTap: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "remove_file_bounce"
    )

    Box(
        modifier = Modifier
            .size(42.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = if (enabled) 1f else 0.35f),
                shape = CircleShape
            )
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        pressed = true
                        val up = waitForUpOrCancellation()
                        pressed = false
                        if (up != null) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTap()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}
