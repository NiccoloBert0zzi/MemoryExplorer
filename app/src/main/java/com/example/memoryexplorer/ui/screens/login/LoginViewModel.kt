package com.example.memoryexplorer.ui.screens.login

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LoginState(
    val email: String,
    val remember: Boolean
)

class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel() {

    private lateinit var auth: FirebaseAuth

    var state by mutableStateOf(LoginState("", false))
        private set

    fun setEmail(value: String) {
        state = LoginState(value, state.remember)
        viewModelScope.launch { repository.setEmail(value) }
    }

    fun setRemember(value: Boolean) {
        state = LoginState(state.email, value)
        viewModelScope.launch { repository.setRemember(value) }
    }

    init {
        viewModelScope.launch {
            state = LoginState(repository.email.first(), repository.remember.first())
        }
    }

    fun onLogin(email: String, password:String, remember: Boolean, navController: NavHostController) {
        setEmail(email)
        setRemember(remember)

        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(MemoryExplorerRoute.Home.route) {
                        popUpTo(MemoryExplorerRoute.Login.route) { inclusive = true }
                    }
                } else {
                    Toast.makeText(
                        navController.context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun onRegister(navController: NavHostController) {
        navController.navigate(MemoryExplorerRoute.Register.route)
    }
}