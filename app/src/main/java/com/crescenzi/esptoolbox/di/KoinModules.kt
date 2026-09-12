package com.crescenzi.esptoolbox.di

import com.crescenzi.esp32.LogRepo
import com.crescenzi.esptoolbox.presentation.DeviceHardwareStatus
import com.crescenzi.esp32.usb.UsbRepo
import com.crescenzi.esp32.firmware.EspRepo
import com.crescenzi.esp32.wifi.EspTouchRepo
import com.crescenzi.esptoolbox.presentation.onboarding.OnboardingViewModel
import com.crescenzi.esptoolbox.presentation.main_shell.logs.LogViewModel
import com.crescenzi.esptoolbox.presentation.main_shell.usb_connection.USBConnectionViewModel
import com.crescenzi.esptoolbox.presentation.main_shell.usb_flash.USBFlashViewModel
import com.crescenzi.esptoolbox.presentation.main_shell.wifi_connection.WIFIViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositories = module {
    single { LogRepo() }
    single { DeviceHardwareStatus() }
    single { EspRepo(get()) }
    single { UsbRepo(get(), get()) }
    single { EspTouchRepo() }
}

val viewModels = module {
    viewModel { OnboardingViewModel(get()) }
    viewModel { LogViewModel(get()) }
    viewModel { USBConnectionViewModel(get(), get(), get(), get()) }
    viewModel { USBFlashViewModel(get(), get(), get()) }
    viewModel { WIFIViewModel(application = get(), deviceHardwareStatus = get(), get(), get()) }
}
