package com.crescenzi.esptoolbox.presentation.main_shell.wifi_connection.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.core.AppConstants.STATUS_DEF_CHAR
import com.crescenzi.esptoolbox.presentation.main_shell.wifi_connection.WIFIViewModel
import com.crescenzi.esptoolbox.presentation.widget.AppTextField


/**
 * Data input section
 */
@Composable
fun DataSection(
    wifiViewModel: WIFIViewModel,
    pwdState: MutableState<String>,
    passwordEnabled: Boolean = true
) {

    val ssid = wifiViewModel.ssid.collectAsStateWithLifecycle()
    val bssid = wifiViewModel.bssid.collectAsStateWithLifecycle()

    /**
     * SSID
     */
    key(ssid.value) {
        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            opt = KeyboardOptions.Default,
            onValueChange = {},
            label = "SSID",
            initialValue = ssid.value.ifEmpty { STATUS_DEF_CHAR },
            enabled = false
        )
    }

    /**
     * BSSID
     */
    key(bssid.value) {
        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            opt = KeyboardOptions.Default,
            onValueChange = {},
            label = "BSSID",
            initialValue = bssid.value.ifEmpty { STATUS_DEF_CHAR },
            enabled = false
        )
    }


    /**
     * Password
     */
    AppTextField(
        modifier = Modifier.fillMaxWidth(),
        opt = KeyboardOptions.Default,
        onValueChange = { pwdState.value = it },
        label = stringResource(R.string.wifi_pwd_hint),
        initialValue = pwdState.value,
        isPassword = true,
        enabled = passwordEnabled
    )

}
