package com.crescenzi.esptoolbox.presentation.main_shell.usb_flash.util


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.presentation.widget.AppButton
import com.crescenzi.esptoolbox.presentation.widget.AppButtonType


/**
 * RESET and FLASH section
 */
@Composable
fun UsbUpdaterButtonsWidget(
    flashEnabled: Boolean,
    resetEnabled: Boolean,
    onReset: () -> Unit,
    onFlash: () -> Unit
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        /**
         * Reset btn
         */
        AppButton(
            txt = stringResource(R.string.btn_reset),
            type = AppButtonType.OUTLINED,
            destructive = true,
            enabled = resetEnabled,
            fillWidth = false,
            onTap = {
                onReset()
            }
        )


        /**
        Flash Btn
         */
        AppButton(
            txt = stringResource(R.string.btn_flash),
            enabled = flashEnabled,
            fillWidth = false,
            onTap = {
                onFlash()
            }
        )

    }
}
