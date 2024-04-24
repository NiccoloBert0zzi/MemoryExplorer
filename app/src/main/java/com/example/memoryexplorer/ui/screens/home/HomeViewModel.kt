package com.example.memoryexplorer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoritesState(val favourites: List<Favourite>)

class HomeViewModel (
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {
    private val _memories = MutableStateFlow<List<Memory>>(emptyList())
    val memories: StateFlow<List<Memory>> = _memories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val state = favouriteRepository.favourites.map { FavoritesState(favourites = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FavoritesState(emptyList())
    )

    init {
        getMemories()
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
        viewModelScope.launch {
            _isLoading.value = true

            val database = Firebase.database
            val myRef = database.getReference("memories")

            myRef.get().addOnSuccessListener { dataSnapshot ->
                val memories = mutableListOf<Memory>()
                for (snapshot in dataSnapshot.children) {
                    val memory = snapshot.getValue(Memory::class.java)
                    // TODO: se la mail creator è uguale a quella dell'utente loggato, non aggiungere il memory alla lista
                    // TODO: controllo se il memory è pubblico o privato
                    memories.add(memory!!)
                }
                _memories.value = memories
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