package com.example.memoryexplorer.ui.screens.home

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class FavouritesState(val favourites: List<Favourite>)

@Suppress("DEPRECATION")
class HomeViewModel(
    private val application: Application,
    private val favouriteRepository: FavouriteRepository,
    loginRepository: LoginRepository
) : ViewModel() {
    private val _memories = MutableStateFlow<List<Memory>>(emptyList())
    val memories: StateFlow<List<Memory>> = _memories

    private val _locationsName = MutableStateFlow<List<String>>(emptyList())
    val locationsName: StateFlow<List<String>> = _locationsName

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var email: String? = null

    val state = favouriteRepository.favourites.map { FavouritesState(favourites = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FavouritesState(emptyList())
    )

    init {
        viewModelScope.launch {
            email = loginRepository.email.first()
            getMemories()
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

    private fun getMemories() {
        _isLoading.value = true

        val database = FirebaseDatabase.getInstance().getReference("memories")
        database.get().addOnSuccessListener { dataSnapshot ->
            val memories = mutableListOf<Memory>()
            val locations = mutableListOf<String>()
            locations.add(application.getString(R.string.world))
            for (snapshot in dataSnapshot.children) {
                val memory = snapshot.getValue(Memory::class.java)
                if (memory?.isPublic == true && memory.creator != email) {
                    memories.add(memory)
                    val geocoder = Geocoder(application, Locale.getDefault())
                    val addresses = try {
                        val lat = memory.latitude?.toDouble() ?: 0.0
                        val long = memory.longitude?.toDouble() ?: 0.0
                        geocoder.getFromLocation(lat, long, 1)
                    } catch (exception: NumberFormatException) {
                        _error.value = exception.localizedMessage
                        null
                    }
                    val locationName = addresses?.get(0)?.countryName
                    if (locationName != null && locationName !in locations) {
                        locations.add(locationName)
                    }
                }
            }
            _memories.value = memories
            _locationsName.value = locations
        }.addOnFailureListener { exception ->
            _error.value = exception.localizedMessage
        }.addOnCompleteListener {
            _isLoading.value = false
        }
    }

    fun getMemoriesByLocation(locationName: String) {
        if (locationName == application.getString(R.string.world)) {
            getMemories()
            return
        }
        _isLoading.value = true

        val database = FirebaseDatabase.getInstance().getReference("memories")
        database.get().addOnSuccessListener { dataSnapshot ->
            val memories = mutableListOf<Memory>()
            for (snapshot in dataSnapshot.children) {
                val memory = snapshot.getValue(Memory::class.java)
                if (memory?.isPublic == true && memory.creator != email) {
                    val geocoder = Geocoder(application, Locale.getDefault())
                    val addresses = try {
                        val lat = memory.latitude?.toDouble() ?: 0.0
                        val long = memory.longitude?.toDouble() ?: 0.0
                        geocoder.getFromLocation(lat, long, 1)
                    } catch (exception: NumberFormatException) {
                        _error.value = exception.localizedMessage
                        null
                    }
                    val locationNameMemory = addresses?.get(0)?.countryName
                    if (locationNameMemory == locationName) {
                        memories.add(memory)
                    }
                }
            }
            _memories.value = memories
        }.addOnFailureListener { exception ->
            _error.value = exception.localizedMessage
        }.addOnCompleteListener {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}