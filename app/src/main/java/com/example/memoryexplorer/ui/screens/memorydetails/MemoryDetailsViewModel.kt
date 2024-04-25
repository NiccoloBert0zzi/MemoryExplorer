package com.example.memoryexplorer.ui.screens.memorydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavouritesState(val favourites: List<Favourite>)

class MemoryDetailsViewModel(
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {
    private val _memory = MutableStateFlow<Memory?>(null)
    val memory: StateFlow<Memory?> = _memory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val state = favouriteRepository.favourites.map { FavouritesState(favourites = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FavouritesState(emptyList())
    )

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

    fun clearError() {
        _error.value = null
    }
}