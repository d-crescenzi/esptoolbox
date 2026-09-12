package com.crescenzi.esptoolbox.presentation.main_shell.logs.util

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.presentation.main_shell.logs.LogViewModel
import com.crescenzi.esptoolbox.presentation.widget.AppButton
import com.crescenzi.esptoolbox.presentation.widget.AppButtonType
import com.crescenzi.esptoolbox.theme.SPACE_S

@Composable
fun LogScreenTopBarWidget(logViewModel: LogViewModel) {

    val localClipboard = LocalClipboard.current
    val logs by logViewModel.logRepo.logs.collectAsStateWithLifecycle()
    val hasLogs = logs.isNotEmpty()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SPACE_S)
    ) {

        /**
         * Clear logs (outlined, red)
         */
        AppButton(
            modifier = Modifier.weight(1f),
            txt = stringResource(R.string.btn_clear),
            enabled = hasLogs,
            type = AppButtonType.OUTLINED,
            destructive = true,
            onTap = {
                logViewModel.logRepo.cleanLog()
            }
        )

        /**
         * Copy to clipboard (filled)
         */
        AppButton(
            modifier = Modifier.weight(1f),
            txt = stringResource(R.string.btn_copy),
            enabled = hasLogs,
            onTap = {
                localClipboard.nativeClipboard.setPrimaryClip(
                    ClipData.newPlainText("Log", logViewModel.logRepo.toString())
                )
            }
        )
    }
}
