package com.example.memoryexplorer.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memoryexplorer.data.database.Favourite
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val favouriteRepository: FavouriteRepository,
    loginRepository: LoginRepository
) : ViewModel() {
    private var email: String? = null
    private var fav: List<Favourite>? = null

    private val _memories = MutableStateFlow<List<Memory>>(emptyList())
    val memories: StateFlow<List<Memory>> = _memories

    private val _favourites = MutableStateFlow<List<Memory>>(emptyList())
    val favourites: StateFlow<List<Memory>> = _favourites

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _profileImage = MutableStateFlow<String?>(null)
    val profileImage: StateFlow<String?> = _profileImage

    init {
        viewModelScope.launch {
            email = loginRepository.email.first()
            fav = favouriteRepository.getAllFavourites()
            getProfile()
            getMemories()
        }
    }

    private fun getProfile() {
        val storage = FirebaseStorage.getInstance()
        val ref: StorageReference = storage.getReference("Images/${email}/profileImage")
        ref.downloadUrl.addOnSuccessListener {
            _profileImage.value = it.toString()
        }.addOnFailureListener {
            _error.value = it.localizedMessage
        }
    }

    fun getMemories() {
        viewModelScope.launch {
            _isLoading.value = true
            val database = FirebaseDatabase.getInstance().getReference("memories")
            database.get().addOnSuccessListener { dataSnapshot ->
                val memories = mutableListOf<Memory>()
                for (snapshot in dataSnapshot.children) {
                    val memory = snapshot.getValue(Memory::class.java)
                    if (memory?.creator == email) {
                        memories.add(memory!!)
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

    fun getFavourites() {
        viewModelScope.launch {
            _isLoading.value = true
            if (fav?.isEmpty() == true) {
                _favourites.value = emptyList()
                _isLoading.value = false
            } else {
                val favourites = mutableListOf<Memory>()
                for (favourite in fav!!) {
                    val database = FirebaseDatabase.getInstance()
                        .getReference("memories/${favourite.memoryId}")
                    database.get().addOnSuccessListener { dataSnapshot ->
                        val favouriteItem = dataSnapshot.getValue(Memory::class.java)
                        favourites.add(favouriteItem!!)
                        _favourites.value = favourites
                    }.addOnFailureListener { exception ->
                        _error.value = exception.localizedMessage
                    }.addOnCompleteListener {
                        if (favourites.size == fav!!.size)
                            _isLoading.value = false
                    }
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}