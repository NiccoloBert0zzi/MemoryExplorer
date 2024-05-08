package com.example.memoryexplorer.ui.utils

import android.Manifest
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
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    if (!isGPSEnabled) {
        printError("GPS is disabled") // TODO error string
        locationService.openLocationSettings()
    }

    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted ->
                locationService.requestCurrentLocation()

            PermissionStatus.Denied -> {
                printError("Permission denied") // TODO error string
                mainActivity.finish()
            }

            PermissionStatus.PermanentlyDenied -> {
                printError("Permission permanently denied") // TODO error string
                mainActivity.finish()
            }

            PermissionStatus.Unknown -> {
                printError("Unknown permission status") // TODO error string
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

// TODO CheckCameraPermission
@Composable
fun CheckCameraPermission() {
    val context = LocalContext.current

    fun printError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    val cameraPermission = rememberPermission(
        Manifest.permission.CAMERA
    ) { status ->
        when (status) {
            PermissionStatus.Granted ->
                printError("Opening camera") // TODO error string

            PermissionStatus.Denied ->
                printError("Permission denied") // TODO error string

            PermissionStatus.PermanentlyDenied ->
                printError("Permission permanently denied") // TODO error string

            PermissionStatus.Unknown ->
                printError("Unknown permission status") // TODO error string
        }
    }

    LaunchedEffect(cameraPermission.status) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }
}
