package com.crescenzi.esptoolbox.presentation.main_shell.wifi_connection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.presentation.util.getMessage
import com.crescenzi.esp32.LogRepo
import com.crescenzi.esptoolbox.presentation.DeviceHardwareStatus
import com.crescenzi.esp32.exception.WifiConnectionException
import com.crescenzi.esp32.usb.model.LogLevel
import com.crescenzi.esp32.wifi.EspTouchRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


/**
 * WiFi connection management for the board (UDP)
 */
class WIFIViewModel(
    application: Application,
    private val deviceHardwareStatus: DeviceHardwareStatus,
    val logRepo: LogRepo,
    private val espTouchRepo: EspTouchRepo
) : AndroidViewModel(application = application) {

    val bssid = deviceHardwareStatus.bssid
    val ssid = deviceHardwareStatus.ssid

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()


    /**
     *
     * If it connects, the MAC is immediately stored in preferences
     */
    fun sendBroadcast(pwd: String) {
        // Empty password is allowed (open networks); only SSID/BSSID are required
        if (!isNetworkInfoAvailable()) return   // If empty, either permissions are missing or an error occurred

        logRepo.plusLog(line = getApplication<Application>().getString(R.string.broadcast_warning), logLevel = LogLevel.WARNING)
        Thread {
            _loading.value = true
            try {

                /**
                 * Mac == null -> Connection failed
                 */
                espTouchRepo.connect(
                    ssid.value,
                    bssid.value,
                    pwd.trim(),
                    getApplication<Application>()
                )?.let { mac ->
                    _loading.value = false
                    logRepo.plusLog(line = getApplication<Application>().getString(R.string.connection_successfully))
                } ?: run {
                    disableLoadingForException()
                }

            } catch (e: Exception) {
                disableLoadingForException(e)
            }
        }.start()
    }

    fun isNetworkInfoAvailable(
        ssid: String = this.ssid.value,
        bssid: String = this.bssid.value
    ): Boolean =
        ssid.isAvailableNetworkValue() && bssid.isAvailableNetworkValue()

    /**
     * Disables loading and logs the exception
     */
    private fun disableLoadingForException(e: Throwable= WifiConnectionException()){
        _loading.value = false
        getApplication<Application>().baseContext?.let { context ->
            logRepo.plusLog(
                line = getMessage(context, e)
                    .toString(), logLevel = LogLevel.ERROR
            )
        }
    }

}

private fun String.isAvailableNetworkValue(): Boolean =
    trim().isNotEmpty() && !trim().equals("null", ignoreCase = true)
