package com.crescenzi.esptoolbox.presentation.main_shell.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crescenzi.esptoolbox.R
import com.crescenzi.esp32.usb.model.LogLevel
import com.crescenzi.esptoolbox.presentation.main_shell.logs.util.LogScreenTopBarWidget
import com.crescenzi.esptoolbox.presentation.widget.AppScaffold
import com.crescenzi.esptoolbox.theme.CARD_RADIUS
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.NAV_PILL_CLEARANCE
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_M

/**
 * Log page,
 * Designed to start immediately after permissions are granted,
 * standalone since it allows external operations like flash/reset
 */


@Composable
fun LogScreen(
    logViewModel: LogViewModel

) {

    val logs by logViewModel.logRepo.logs.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()


    /**
     * Auto-scroll boilerplate
     */
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val shouldScroll = lastVisibleItem >= logs.lastIndex - 3 // scroll only if already near the bottom
            if (shouldScroll) {
                listState.animateScrollToItem(logs.lastIndex)
            }
        }
    }


    AppScaffold(
        title = stringResource(R.string.log_title),
        reserveTopBarSpace = true,
        scrollable = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LATERAL_PADDING)
                .padding(top = CONTENT_TOP_PADDING, bottom = NAV_PILL_CLEARANCE + SPACE_L),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(CARD_RADIUS)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(CARD_RADIUS)
                    )
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SPACE_M)
                ) {
                    itemsIndexed(logs) { _, log ->
                        SelectionContainer {
                            Text(
                                text = log.line,
                                style = MaterialTheme.typography.bodySmall,
                                color = getLogColor(log.logLevel),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(SPACE_M))
            LogScreenTopBarWidget(logViewModel)
        }
    }
}

@Composable
fun getLogColor(logLevel: LogLevel): Color =
    when (logLevel) {
        LogLevel.INFO -> Color(0xFF008542)
        LogLevel.ERROR -> MaterialTheme.colorScheme.error
        LogLevel.WARNING -> Color(0xFFB57F00)
    }
