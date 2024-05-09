package com.example.memoryexplorer.ui.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

interface CameraLauncher {
    fun captureImage()
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun rememberCameraLauncher(): Pair<CameraLauncher, Bitmap?> {
    var bitmapState by rememberSaveable { mutableStateOf<Bitmap?>(null) }

    val cameraActivityLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                bitmapState = bitmap
            }
        }

    val cameraLauncher by remember {
        derivedStateOf {
            object : CameraLauncher {
                override fun captureImage() {
                    cameraActivityLauncher.launch(null)
                }
            }
        }
    }

    return Pair(cameraLauncher, bitmapState)
}
