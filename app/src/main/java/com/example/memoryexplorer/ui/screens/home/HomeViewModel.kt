package com.example.memoryexplorer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoritesState(val favourites: List<Favourite>)

class HomeViewModel (
    private val favouriteRepository: FavouriteRepository,
    loginRepository: LoginRepository
) : ViewModel() {
    private val _memories = MutableStateFlow<List<Memory>>(emptyList())
    val memories: StateFlow<List<Memory>> = _memories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val email: StateFlow<String> = loginRepository.email.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ""
    )

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

            val database = FirebaseDatabase.getInstance().getReference("memories")

            database.get().addOnSuccessListener { dataSnapshot ->
                val memories = mutableListOf<Memory>()
                for (snapshot in dataSnapshot.children) {
                    val memory = snapshot.getValue(Memory::class.java)
                    if(memory?.isPublic == true && memory.creator != email.value) {
                        memories.add(memory)
                    }
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