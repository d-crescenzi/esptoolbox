package com.crescenzi.esptoolbox.presentation.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.theme.CARD_RADIUS
import com.crescenzi.esptoolbox.theme.SPACE_L
import com.crescenzi.esptoolbox.theme.SPACE_M
import com.crescenzi.esptoolbox.theme.SPACE_XS

private val TILE_ROW_MIN_HEIGHT = 56.dp
private val TILE_GLYPH_SIZE = 36.dp
private val TILE_GLYPH_ICON_SIZE = 20.dp


/**
 * Represents a tile with a leading view, title and subtitle
 */
@Composable
fun LeadingTile(
    modifier: Modifier = Modifier,
    currentValue: Boolean,
    @StringRes titleRes: Int,
    @StringRes subTitleRes: Int? = null,
) {
    val icon = if (currentValue) Icons.Rounded.Check else Icons.Rounded.ErrorOutline
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CARD_RADIUS),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TILE_ROW_MIN_HEIGHT)
                .padding(horizontal = SPACE_L, vertical = SPACE_M),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SPACE_L)
        ) {
            Box(
                modifier = Modifier
                    .size(TILE_GLYPH_SIZE)
                    .background(colorScheme.secondary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.onSecondary,
                    modifier = Modifier.size(TILE_GLYPH_ICON_SIZE)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(SPACE_XS)) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface
                )
                subTitleRes?.let { txtRes ->
                    Text(
                        text = stringResource(txtRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
