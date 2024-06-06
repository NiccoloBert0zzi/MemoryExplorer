package com.example.memoryexplorer.ui.screens.memorydetails

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.utils.MyMarker
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

data class FavouritesState(val favourites: List<Favourite>)

@Suppress("DEPRECATION")
class MemoryDetailsViewModel(
    private val favouriteRepository: FavouriteRepository,
    loginRepository: LoginRepository
) : ViewModel() {
    private val _memory = MutableStateFlow<Memory?>(null)
    val memory: StateFlow<Memory?> = _memory

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val state = favouriteRepository.favourites.map { FavouritesState(favourites = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FavouritesState(emptyList())
    )

    init {
        viewModelScope.launch {
            _email.value = loginRepository.email.first()
        }
    }

    fun getMemoryById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val database = FirebaseDatabase.getInstance().getReference("memories")
            database.child(id).get().addOnSuccessListener { dataSnapshot ->
                val memory = dataSnapshot.getValue(Memory::class.java)
                _memory.value = memory
            }.addOnFailureListener { exception ->
                _error.value = exception.localizedMessage
            }.addOnCompleteListener {
                _isLoading.value = false
            }
        }
    }

    fun addFavourite(memoryId: String) {
        viewModelScope.launch {
            favouriteRepository.upsert(Favourite(memoryId))
        }
    }

    fun removeFavourite(memoryId: String) {
        viewModelScope.launch {
            favouriteRepository.delete(Favourite(memoryId))
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun openMap(latitude: String, longitude: String, mapView: MapView, context: Context) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.isClickable = true
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)

        val lat = latitude.toDouble()
        val lon = longitude.toDouble()
        val currentLocation = GeoPoint(lat, lon)
        val mapController = mapView.controller
        mapController.setZoom(7.0)
        mapController.setCenter(currentLocation)
        val startMarker = Marker(mapView)
        startMarker.icon = BitmapDrawable(context.resources, MyMarker(context).getSmallMarker())
        if (setLocation(lat, lon, context) != null) {
            startMarker.title = setLocation(lat, lon, context)!!.countryName
            startMarker.snippet = setLocation(lat, lon, context)!!.locality
        }
        startMarker.position = currentLocation
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(startMarker)
    }

    private fun setLocation(lat: Double, lon: Double, context: Context): Address? {
        val gcd = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>? = gcd.getFromLocation(lat, lon, 1)
        assert(addresses != null)
        return if (addresses!!.isNotEmpty()) {
            addresses[0]
        } else null
    }

    fun deleteMemory(memoryId: String) {
        viewModelScope.launch {
            val database = FirebaseDatabase.getInstance().getReference("memories")
            database.child(memoryId).removeValue().addOnSuccessListener {
                _error.value = "Memory deleted successfully"
            }.addOnFailureListener {
                _error.value = it.localizedMessage
            }
        }
    }

}
