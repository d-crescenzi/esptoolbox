package com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.util

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.presentation.widget.AppTextField


@Composable
fun UsbConnectionCredentialsWidget(ssidState: MutableState<String>, passwordState: MutableState<String>) {
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    AppTextField(
        opt = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        actions = KeyboardActions(
            onNext = { passwordFocusRequester.requestFocus() }
        ),
        onValueChange = { ssidState.value = it },
        label = stringResource(R.string.ssid_placeholder),
        initialValue = ssidState.value
    )

    AppTextField(
        modifier = Modifier.focusRequester(passwordFocusRequester),
        opt = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        actions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        onValueChange = { passwordState.value = it },
        label = stringResource(R.string.pwd_placeholder),
        initialValue = passwordState.value,
        isPassword = true
    )
}
