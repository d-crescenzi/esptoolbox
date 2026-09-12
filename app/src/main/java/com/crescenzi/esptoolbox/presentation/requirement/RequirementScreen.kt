package com.crescenzi.esptoolbox.presentation.requirement

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.crescenzi.esptoolbox.presentation.widget.AppScaffold
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING

/**
 * Generic blocking page shown when a requirement is lost while using the app,
 * title and subtitle are passed from the outside based on what is missing
 */
@Composable
fun RequirementScreen(
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int
) {

    AppScaffold(
        title = stringResource(titleRes),
        reserveTopBarSpace = true,
        scrollable = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LATERAL_PADDING)
                .padding(top = CONTENT_TOP_PADDING)
        ) {
            Text(
                text = stringResource(subtitleRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
