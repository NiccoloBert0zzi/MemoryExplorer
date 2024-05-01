package com.example.memoryexplorer.ui.screens.addmemory

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
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
import java.io.File
import java.io.FileOutputStream

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
        image: Bitmap?,
        navController: NavController
    ) {
        _isLoading.value = true
        val database = FirebaseDatabase.getInstance().getReference("memories")
        val id = database.push().key

        if (id != null) {
            val storageRef = Firebase.storage.getReference("Images/$email/memoriesImage/$id")

            // Convert the Bitmap to a Uri
            val imageUri = bitmapToUri(image, navController)

            storageRef.putFile(imageUri!!)
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
        val fileOutputStream = FileOutputStream(file as File)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        // Get the Uri of the file
        return Uri.fromFile(file)
    }

    fun clearError() {
        _error.value = null
    }

}