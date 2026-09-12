package com.crescenzi.esptoolbox.presentation.main_shell.wifi_connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.presentation.main_shell.wifi_connection.util.DataSection
import com.crescenzi.esptoolbox.presentation.widget.AppButton
import com.crescenzi.esptoolbox.presentation.widget.AppScaffold
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.NAV_PILL_CLEARANCE
import com.crescenzi.esptoolbox.theme.SPACE_L

/**
 * WiFi Connection Screen
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WIFIConnectionScreen(
    wifiViewModel: WIFIViewModel
) {

    val loading by wifiViewModel.loading.collectAsStateWithLifecycle()
    val ssid by wifiViewModel.ssid.collectAsStateWithLifecycle()
    val bssid by wifiViewModel.bssid.collectAsStateWithLifecycle()
    val pwdState = rememberSaveable { mutableStateOf("") }
    val canConnect = !loading && wifiViewModel.isNetworkInfoAvailable(ssid, bssid)


    Box(modifier = Modifier.fillMaxSize()) {

        AppScaffold(
            title = stringResource(R.string.wifi_title),
            reserveTopBarSpace = true,
            scrollable = false,
            bottomBar = {
                AppButton(
                    modifier = Modifier.padding(bottom = NAV_PILL_CLEARANCE),
                    txt = stringResource(R.string.btn_connect),
                    enabled = canConnect,
                    onTap = {
                        wifiViewModel.sendBroadcast(pwdState.value)
                    }
                )
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(horizontal = LATERAL_PADDING)
                    .padding(top = CONTENT_TOP_PADDING)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(SPACE_L)) {
                    DataSection(wifiViewModel, pwdState)
                }
            }

        }

        if (loading) {
            LoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
