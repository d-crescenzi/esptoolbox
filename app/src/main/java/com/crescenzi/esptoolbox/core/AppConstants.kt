package com.crescenzi.esptoolbox.core

import android.Manifest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crescenzi.esptoolbox.BuildConfig

/**
 * All app-wide variables
 */
object AppConstants {

    /**
     * Business
     */
    const val STATUS_DEF_CHAR = "—"

    const val WRITE_WAIT_MILLIS = 2000  //For the serial port
    const val INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB"
    const val OBSERVE_STATUS_DELAY = 3000L

    val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * Ui
     */
    val NAV_DOT_COLOR = Color.White
    val NAV_DOT_OFFSET = 2.dp

    const val PICK_MIME_TYPE="application/octet-stream"
}
