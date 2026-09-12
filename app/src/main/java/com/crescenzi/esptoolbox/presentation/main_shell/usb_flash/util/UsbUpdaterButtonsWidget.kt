package com.crescenzi.esptoolbox.presentation.main_shell.usb_flash.util


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.presentation.widget.AppButton
import com.crescenzi.esptoolbox.presentation.widget.AppButtonType
import com.crescenzi.esptoolbox.theme.SPACE_S


/**
 * RESET and FLASH section
 */
@Composable
fun UsbUpdaterButtonsWidget(
    modifier: Modifier = Modifier,
    flashEnabled: Boolean,
    resetEnabled: Boolean,
    onReset: () -> Unit,
    onFlash: () -> Unit
) {


    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SPACE_S)
    ) {

        /**
         * Reset btn
         */
        AppButton(
            modifier = Modifier.weight(1f),
            txt = stringResource(R.string.btn_reset),
            type = AppButtonType.OUTLINED,
            destructive = true,
            enabled = resetEnabled,
            onTap = {
                onReset()
            }
        )


        /**
        Flash Btn
         */
        AppButton(
            modifier = Modifier.weight(1f),
            txt = stringResource(R.string.btn_flash),
            enabled = flashEnabled,
            onTap = {
                onFlash()
            }
        )

    }
}
