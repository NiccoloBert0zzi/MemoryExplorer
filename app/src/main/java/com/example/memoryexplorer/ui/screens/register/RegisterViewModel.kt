package com.example.memoryexplorer.ui.screens.register

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch

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
        image: Int,
        navController: NavHostController
    ) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(
                navController.context,
                R.string.empty_fields,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(
                navController.context,
                R.string.password_length,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val storageRef = Firebase.storage.getReference("Images/$email/profileImage")
                    // TODO: Add dynamic image picker
                    val imageUri = "android.resource://com.example.memoryexplorer/drawable/$image"
                    storageRef.putFile(imageUri.toUri())
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                navController.context,
                                e.message.toString(),
                                Toast.LENGTH_SHORT
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
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}