package com.example.memoryexplorer.ui.screens.register

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class RegisterState(
    val email: String,
    val username: String,
    val password: String,
    val remember: Boolean
)

class RegisterViewModel(
    private val repository: LoginRepository
) : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private var state by mutableStateOf(RegisterState("", "", "", false))

    private fun setEmail(value: String) {
        state = RegisterState(value, state.username, state.password, state.remember)
        viewModelScope.launch { repository.setEmail(value) }
    }

    private fun setRemember(value: Boolean) {
        state = RegisterState(state.email, state.username, state.password, value)
        viewModelScope.launch { repository.setRemember(value) }
    }

    fun onLogin(navController: NavHostController) {
        navController.navigateUp()
    }

    fun onRegister(
        email: String,
        username: String, // TODO: Save username in Firebase
        password: String,
        remember: Boolean,
        image: Bitmap?,
        navController: NavHostController
    ) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(
                navController.context,
                R.string.empty_fields,
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(
                navController.context,
                R.string.password_length,
                Toast.LENGTH_LONG
            ).show()
            return
        }
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val storageRef = Firebase.storage.getReference("Images/$email/profileImage")
                    val imageUri = bitmapToUri(image, navController)
                    storageRef.putFile(imageUri!!)
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                navController.context,
                                e.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnCompleteListener {
                            setEmail(email)
                            setRemember(remember)
                            navController.navigate(MemoryExplorerRoute.Home.route) {
                                popUpTo(MemoryExplorerRoute.Login.route) { inclusive = true }
                            }
                        }
                } else {
                    Toast.makeText(
                        navController.context,
                        task.exception?.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
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
}