package com.crescenzi.esptoolbox.presentation.main_shell.usb_flash

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.crescenzi.esptoolbox.R
import com.crescenzi.esptoolbox.core.LOG
import com.crescenzi.esptoolbox.presentation.util.getMessage
import com.crescenzi.esp32.LogRepo
import com.crescenzi.esp32.params.BaudRateFormat
import com.crescenzi.esp32.usb.UsbRepo
import com.crescenzi.esp32.usb.model.LogLevel
import com.crescenzi.esp32.firmware.EspRepo
import com.crescenzi.esp32.firmware.EspCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock

/**
 * Handles operations such as Flash
 */
class USBFlashViewModel(
    private val usbRepo: UsbRepo,
    private val espRepo: EspRepo,
    val logRepo: LogRepo
) : ViewModel() {
    private val defaultAddresses = listOf(0x8000, 0x1000, 0x10000, 0x9000, 0x20000, 0x300000)

    companion object {
        const val MAX_FLASH_FILES = 6
    }

    private val _baudRate = MutableStateFlow(BaudRateFormat.B115200)
    val baudRate = _baudRate.asStateFlow()

    fun updateBaudRate(baudRateFormat: BaudRateFormat) {
        _baudRate.value = baudRateFormat
    }

    private val _flashFiles = MutableStateFlow(emptyList<FlashFileEntry>())
    val flashFiles = _flashFiles.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun addFlashFile(label: String, uri: Uri) {
        val current = _flashFiles.value
        if (current.size >= MAX_FLASH_FILES) return

        _flashFiles.value = current + FlashFileEntry(
            label = label,
            address = defaultAddresses.getOrElse(current.size) { 0x10000 },
            uri = uri
        )
    }

    fun removeFlashFile(index: Int) {
        val current = _flashFiles.value.toMutableList()
        if (index !in current.indices) return
        current.removeAt(index)
        _flashFiles.value = current
    }

    fun updateFlashAddress(index: Int, address: Int?, addressValid: Boolean) {
        val current = _flashFiles.value.toMutableList()
        if (index !in current.indices) return
        current[index] = current[index].copy(
            address = address ?: current[index].address,
            addressValid = addressValid
        )
        _flashFiles.value = current
    }



    init {
        espRepo.setEspCallback(espCallback = object : EspCallback {
            override fun onInfo(line: String) {
                LOG("On Info $line")
                logRepo.plusLog(line, LogLevel.INFO)
            }


            override fun onFlashLoading(percentage: Int) {
                logRepo.plusLog("$percentage %", LogLevel.INFO)
            }

            override fun onError(e: Throwable) {
                LOG("Exception ${e.message.toString()}")
                logRepo.plusLog(e.message.toString(), LogLevel.ERROR)
            }

        })
    }

    private val flashLock = ReentrantLock()


    fun commandReset() = usbRepo.reset()

    /**
     * Firmware flash
     * CANNOT INTERCEPT THE PERMISSION REQUEST
     */
    fun flash(
        context: Context
    ) {
        Thread {
            var acquired = false
            try {
                acquired = flashLock.tryLock()
                if (!acquired) {
                    logRepo.plusLog(
                        context.getString(R.string.flash_in_progress_warning),
                        LogLevel.WARNING
                    )
                    return@Thread
                }

                _loading.value = true

                espRepo.setBaudRateCallback { _baudRate.value }

                if (espRepo.chipValidation()) {
                    for (item in _flashFiles.value.filter { it.uri != null && it.addressValid }) {
                        logRepo.plusLog(
                            context.getString(R.string.flash_do_not_disconnect_usb),
                            LogLevel.WARNING
                        )
                        item.uri?.let {
                            LOG(_flashFiles.value.toString())


                            val firmware: InputStream =
                                context.contentResolver.openInputStream(it) ?: return@let

                            espRepo.apply {
                                changeBaudRate()
                                init()
                                readFile(firmware)?.let { byteArray ->
                                    espRepo.flashFirmware(byteArray, item.address)
                                }
                            }
                        }
                    }
                    logRepo.plusLog(
                        context.getString(R.string.flash_rst), LogLevel.WARNING
                    )
                    _loading.value = false

                } else {
                    espRepo.reqPermission()
                    _loading.value = false
                }

            } catch (e: Exception) {
                _loading.value = false
                logRepo.plusLog(getMessage(context, e), LogLevel.ERROR)
            } finally {
                if (acquired) {
                    flashLock.unlock()
                }
            }
        }.start()
    }


}
