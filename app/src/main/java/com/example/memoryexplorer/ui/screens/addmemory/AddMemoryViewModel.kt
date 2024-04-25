package com.example.memoryexplorer.ui.screens.addmemory

import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddMemoryViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var email: String? = null

    init {
        viewModelScope.launch {
            email = loginRepository.email.first()
        }
    }

    fun onAddMemory(
        title: String,
        description: String,
        date: String,
        public: Boolean,
        image: Int,
        navController: NavController
    ) {
        _isLoading.value = true
        val database = FirebaseDatabase.getInstance().getReference("memories")
        val id = database.push().key

        if (id != null) {
            val storageRef = Firebase.storage.getReference("Images/$email/memoriesImage/$id")
            // TODO: Add dynamic image picker
            val imageUri = "android.resource://com.example.memoryexplorer/drawable/$image"

            storageRef.putFile(imageUri.toUri())
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        val downloadedImageUri = downloadUri.toString()
                        // TODO: Add dynamic location picker
                        val memory = Memory(id, email, title, description, date, "latitude", "longitude", downloadedImageUri, public)
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
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnCompleteListener {
                    _isLoading.value = false
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

}