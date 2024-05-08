package com.example.memoryexplorer.ui.screens.addmemory

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.memoryexplorer.R
import com.example.memoryexplorer.ui.utils.LocationService
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddMemoryScreen(
    navController: NavHostController,
    addMemoryViewModel: AddMemoryViewModel
) {
    val isLoading by addMemoryViewModel.isLoading.collectAsState()
    val error by addMemoryViewModel.error.collectAsState()

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    var public by rememberSaveable { mutableStateOf(false) }
    val latitude by addMemoryViewModel.latitude.collectAsState()
    val longitude by addMemoryViewModel.longitude.collectAsState()

    var bitmapState by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                bitmapState = bitmap
            }
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { contentPadding ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.add_memory_title),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        label = { Text(stringResource(R.string.memory_title)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.size(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.add_memory_image),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = bitmapState?.let { BitmapPainter(it.asImageBitmap()) }
                            ?: painterResource(R.drawable.default_memory),
                        contentDescription = "Memory image",
                        modifier = Modifier
                            .clickable {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        cameraPermission
                                    ) -> {
                                        takePictureLauncher.launch(null)
                                    }

                                    else -> {
                                        Toast.makeText(
                                            context,
                                            "Camera permission is required to take pictures.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    )
                }
                Spacer(Modifier.size(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.add_memory_description),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        label = { Text(stringResource(R.string.memory_description)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.size(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.add_memory_date),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        label = { Text(stringResource(R.string.memory_date)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.size(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = public,
                        onCheckedChange = { public = it }
                    )
                    Text(
                        stringResource(R.string.memory_public),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.size(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.add_memory_position),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.size(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    OsmMapView(
                        addMemoryViewModel,
                        latitude,
                        longitude
                    )
                }
                Spacer(Modifier.size(100.dp))
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = {
                    val imageToUse = bitmapState ?: BitmapFactory.decodeResource(
                        navController.context.resources,
                        R.drawable.default_memory
                    )
                    addMemoryViewModel.onAddMemory(
                        title,
                        description,
                        date,
                        public,
                        imageToUse,
                        latitude.toString(),
                        longitude.toString(),
                        navController
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                enabled = !isLoading
            ) {
                Text(
                    stringResource(R.string.add_memory),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    if (error != null) {
        Toast.makeText(navController.context, error, Toast.LENGTH_LONG).show()
        addMemoryViewModel.clearError()
    }
}

@Composable
fun OsmMapView(addMemoryViewModel: AddMemoryViewModel, latitude: Double, longitude: Double) {
    val locationService = LocationService(LocalContext.current)
    var lat = if (latitude == 0.0) locationService.coordinates?.latitude else latitude
    var lon = if (longitude == 0.0) locationService.coordinates?.longitude else longitude
    val currentLocation = lat?.let { lon?.let { it1 -> GeoPoint(it, it1) } }
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                if (currentLocation != null) {
                    addMemoryViewModel.loadMap(this, currentLocation, context)
                }
                this.overlayManager.add(MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            lat = p.latitude
                            lon = p.longitude
                            addMemoryViewModel.setMarker(
                                this@apply,
                                GeoPoint(lat!!, lon!!),
                                context
                            )
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }))
            }
        }
    )
}