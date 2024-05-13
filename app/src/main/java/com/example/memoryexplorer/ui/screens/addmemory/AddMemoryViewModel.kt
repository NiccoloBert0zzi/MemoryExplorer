package com.example.memoryexplorer.ui.screens.addmemory

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.getLocationService
import com.example.memoryexplorer.ui.utils.MyMarker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

@Suppress("DEPRECATION")
class AddMemoryViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var email: String? = null

    private val _latitude = MutableStateFlow(0.0)
    var latitude: StateFlow<Double> = _latitude
    private val _longitude = MutableStateFlow(0.0)
    var longitude: StateFlow<Double> = _longitude

    private var marker: Marker? = null

    init {
        viewModelScope.launch {
            email = loginRepository.email.first()
            getLocationService().coordinates?.let {
                _latitude.value = it.latitude
                _longitude.value = it.longitude
            }
        }
    }

    fun onAddMemory(
        title: String,
        description: String,
        date: String,
        public: Boolean,
        image: Bitmap?,
        latitude: String,
        longitude: String,
        navController: NavController
    ) {
        _isLoading.value = true

        if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
            _error.value = "Please fill in all the fields" // TODO
            _isLoading.value = false
            return
        }

        val database = FirebaseDatabase.getInstance().getReference("memories")
        val id = database.push().key

        if (id != null) {
            val storageRef = Firebase.storage.getReference("Images/$email/memoriesImage/$id")

            val imageUri = bitmapToUri(image, navController)

            storageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        val downloadedImageUri = downloadUri.toString()
                        val memory = Memory(
                            id,
                            email,
                            title,
                            description,
                            date,
                            latitude,
                            longitude,
                            downloadedImageUri,
                            public
                        )
                        database.child(id).setValue(memory)
                            .addOnSuccessListener {
                                navController.navigateUp()
                            }.addOnFailureListener { exception ->
                                _error.value = exception.localizedMessage
                            }.addOnCompleteListener {
                                _isLoading.value = false
                            }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        navController.context,
                        exception.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnCompleteListener {
                    _isLoading.value = false
                }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap?, navController: NavController): Uri? {
        // Check if the bitmap is null
        if (bitmap == null) {
            return null
        }

        // Create a file in the cache directory
        val file = File(navController.context.cacheDir, "${System.currentTimeMillis()}.jpg")

        // Write the bitmap to the file
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        // Get the Uri of the file
        return Uri.fromFile(file)
    }

    fun clearError() {
        _error.value = null
    }

    fun loadMap(mapView: MapView, currentLocation: GeoPoint, context: Context) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.isClickable = true
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        setMarker(mapView, currentLocation, context)
        val mapController = mapView.controller
        mapController.setZoom(7.0)
        mapController.setCenter(currentLocation)
    }

    @Throws(IOException::class)
    fun setMarker(mapView: MapView, currentLocation: GeoPoint, context: Context) {
        mapView.overlays.remove(marker)
        marker = Marker(mapView)
        marker!!.icon = BitmapDrawable(context.resources, MyMarker(context).getSmallMarker())
        setLocation(
            currentLocation.latitude,
            currentLocation.longitude,
            context
        )?.let { location ->
            marker!!.title = location.countryName
            marker!!.snippet = location.locality
        }
        marker!!.position = currentLocation
        marker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        _latitude.value = currentLocation.latitude
        _longitude.value = currentLocation.longitude
    }

    private fun setLocation(lat: Double, lon: Double, context: Context): Address? {
        val gcd = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = gcd.getFromLocation(lat, lon, 1)
        assert(addresses != null)
        return if (addresses!!.isNotEmpty()) {
            addresses[0]
        } else null
    }
}