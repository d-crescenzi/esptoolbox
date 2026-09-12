package com.crescenzi.esptoolbox.presentation.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.crescenzi.esptoolbox.theme.CONTENT_TOP_PADDING
import com.crescenzi.esptoolbox.theme.LATERAL_PADDING
import com.crescenzi.esptoolbox.theme.SPACE_L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    title: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    scrollable: Boolean = true,
    titleOnPage: Boolean = true,
    reserveTopBarSpace: Boolean = false,
    contentWindowInsets: WindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
    contentPadding: PaddingValues = PaddingValues(
        start = LATERAL_PADDING,
        end = LATERAL_PADDING,
        top = CONTENT_TOP_PADDING,
        bottom = SPACE_L
    ),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(SPACE_L),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    bottomBar: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val barShown = title != null || trailing != null || reserveTopBarSpace
    val scrollState = rememberScrollState()
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = contentWindowInsets,
            topBar = {
                if (barShown) {
                    AppTopBar(
                        title = title.orEmpty(),
                        trailing = trailing,
                    )
                }
            },
            bottomBar = {
                if (bottomBar != null) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = LATERAL_PADDING, vertical = SPACE_L),
                        verticalArrangement = Arrangement.spacedBy(SPACE_L),
                        content = bottomBar,
                    )
                }
            },
        ) { insets ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(insets)
                        .consumeWindowInsets(insets)
                        .then(
                            if (scrollable) {
                                Modifier
                                    .verticalScroll(scrollState)
                                    .imePadding()
                                    .padding(contentPadding)
                            } else {
                                Modifier
                            },
                        ),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    trailing: (@Composable () -> Unit)? = null,
) {
    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        actions = { trailing?.invoke() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}
