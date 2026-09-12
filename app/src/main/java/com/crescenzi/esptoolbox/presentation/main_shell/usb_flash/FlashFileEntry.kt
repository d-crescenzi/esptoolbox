package com.crescenzi.esptoolbox.presentation.main_shell.usb_flash

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Represents each unit for flashing
 */
@Parcelize
data class FlashFileEntry(
    val label: String=".bin",       // == e.g. "Bootloader", "Firmware", etc. == //
    val address: Int, // == e.g. 0x1000 == //
    val addressValid: Boolean = true,
    var uri: Uri?=null
): Parcelable
