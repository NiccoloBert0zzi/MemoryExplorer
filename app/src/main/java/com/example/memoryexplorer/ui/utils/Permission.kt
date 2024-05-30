package com.example.memoryexplorer.ui.utils

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.memoryexplorer.MainActivity
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.models.PermissionStatus

interface PermissionHandler {
    val permission: String
    val status: PermissionStatus
    fun launchPermissionRequest()
}

@Composable
fun rememberPermission(
    permission: String,
    onResult: (status: PermissionStatus) -> Unit = {}
): PermissionHandler {
    var status by remember { mutableStateOf(PermissionStatus.Unknown) }

    val activity = (LocalContext.current as ComponentActivity)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        status = when {
            isGranted -> PermissionStatus.Granted
            activity.shouldShowRequestPermissionRationale(permission) -> PermissionStatus.Denied
            else -> PermissionStatus.PermanentlyDenied
        }
        onResult(status)
    }

    val permissionHandler by remember {
        derivedStateOf {
            object : PermissionHandler {
                override val permission = permission
                override val status = status
                override fun launchPermissionRequest() = permissionLauncher.launch(permission)
            }
        }
    }
    return permissionHandler
}

@Composable
fun CheckLocationPermission(mainActivity: MainActivity, locationService: LocationService) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    fun printError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    if (!isGPSEnabled) {
        printError(context.getString(R.string.gps_disabled))
        locationService.openLocationSettings()
    }

    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted ->
                locationService.requestCurrentLocation()

            PermissionStatus.Denied -> {
                printError(context.getString(R.string.permission_denied))
                mainActivity.finish()
            }

            PermissionStatus.PermanentlyDenied -> {
                printError(context.getString(R.string.permission_permanently_denied))
                mainActivity.finish()
            }

            PermissionStatus.Unknown -> {
                printError(context.getString(R.string.permission_unknow))
                mainActivity.finish()
            }
        }
    }

    LaunchedEffect(locationPermission.status) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
    }
}

@Composable
fun CheckNotificationPermission() {
    val context = LocalContext.current
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val areNotificationsEnabled = notificationManager.areNotificationsEnabled()

    fun printError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    if (!areNotificationsEnabled) {
        printError(context.getString(R.string.notifications_disabled))
    }
}