package com.crescenzi.esptoolbox

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.net.Uri
import android.net.ConnectivityManager
import android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.crescenzi.esptoolbox.core.checkStoreUpdate
import com.crescenzi.esptoolbox.core.AppConstants.INTENT_ACTION_GRANT_USB
import com.crescenzi.esptoolbox.core.AppConstants.permissions
import com.crescenzi.esptoolbox.core.clearFocusOnTapOutside
import com.crescenzi.esptoolbox.presentation.DeviceHardwareStatus
import com.crescenzi.esp32.usb.UsbRepo
import com.crescenzi.esptoolbox.presentation.main_shell.MainShell
import com.crescenzi.esptoolbox.system.GenericReceiver
import com.crescenzi.esptoolbox.system.SsidReceiver
import com.crescenzi.esptoolbox.system.UsbPermissionReceiver
import com.crescenzi.esptoolbox.theme.AppTheme
import org.koin.android.ext.android.inject

/**
 * Device Connection Activity
 */
class MainActivity : ComponentActivity() {
    companion object {
        private const val PERMISSION_PREFS = "permission_prefs"
        private const val KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested"
    }

    private val ssidReceiver = SsidReceiver()
    private val usbPermissionReceiver = UsbPermissionReceiver()
    private val genericReceiver = GenericReceiver()
    private var ssidReceiverRegistered = false

    private val deviceHardwareStatus: DeviceHardwareStatus by inject()
    private val usbRepo: UsbRepo by inject()


    /**
     * Once location permissions are granted, we start observing the SSID
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap: Map<String, Boolean> ->
        getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_LOCATION_PERMISSION_REQUESTED, true)
            .apply()

        val coarseGranted = permissionsMap[permissions[0]] == true
        val fineGranted = permissionsMap[permissions[1]] == true

        if (coarseGranted && fineGranted) {
            registerSsidReceiverIfNeeded()
            deviceHardwareStatus.changeLocationPermissionStatus(true)
        } else {
            deviceHardwareStatus.changeLocationPermissionStatus(false)
            if (isLocationPermissionPermanentlyDenied()) {
                openAppSettings()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        /**
         * Force the app to dark at OS level (uiMode) + light system bar icons
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        /**
         * Update check
         */
        checkStoreUpdate()

        ContextCompat.registerReceiver(
            this,
            usbPermissionReceiver,
            IntentFilter(INTENT_ACTION_GRANT_USB),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        /**
         * Handles data/Wi-Fi connection & location by updating the Repo
         */
        registerReceiver(genericReceiver, IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        })

        /**
         * Location init (the first value is not received)
         */
        deviceHardwareStatus.changeLocationStatus(
            (getSystemService(LOCATION_SERVICE) as LocationManager).isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        )

        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .clearFocusOnTapOutside(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    content = { safePadding ->
                        Column(
                            modifier = Modifier
                                .padding(safePadding)
                                .fillMaxSize()
                        ) {
                            MainShell(
                                onReqUsbPermission = this@MainActivity::requestUsbPermission,
                                onReqLocationPermission = this@MainActivity::requestLocationPermission
                            )
                        }
                    })
            }
        }
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionPermanentlyDenied()) {
            openAppSettings()
        } else {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }


    /**
     * Permission request for each different device, WORKING VERSION FOR ALL API LEVELS
     */
    fun requestUsbPermission() {
        usbRepo._currentDevice.value?.let {
            val usbManager = getSystemService(USB_SERVICE) as UsbManager
            val intent = Intent(INTENT_ACTION_GRANT_USB).apply {
                setPackage(packageName)
            }
            usbManager.requestPermission(
                it.device, PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            )
        }
    }


    private fun checkPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun hasLocationPermissions() =
        permissions.all { checkPermission(it) }

    private fun isLocationPermissionPermanentlyDenied(): Boolean {
        val alreadyRequested = getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE)
            .getBoolean(KEY_LOCATION_PERMISSION_REQUESTED, false)

        return alreadyRequested && permissions.any { permission ->
            !checkPermission(permission) && !shouldShowRequestPermissionRationale(permission)
        }
    }

    private fun openAppSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        )
    }

    private fun registerSsidReceiverIfNeeded() {
        if (ssidReceiverRegistered) return
        registerReceiver(
            ssidReceiver,
            IntentFilter(NETWORK_STATE_CHANGED_ACTION)
        )
        ssidReceiverRegistered = true
    }

    override fun onResume() {
        super.onResume()

        if (hasLocationPermissions()) {
            registerSsidReceiverIfNeeded()
            deviceHardwareStatus.changeLocationPermissionStatus(true)
        } else
            deviceHardwareStatus.changeLocationPermissionStatus(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbPermissionReceiver)
        if (ssidReceiverRegistered) {
            unregisterReceiver(ssidReceiver)
        }
        unregisterReceiver(genericReceiver)
    }


}
