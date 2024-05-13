package com.example.memoryexplorer.ui.screens.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class LoginState(
    val email: String,
    val password: String,
    val remember: Boolean
)

class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private var state by mutableStateOf(LoginState("", "",false))
    private fun setEmail(value: String) {
        state = LoginState(value, state.password, state.remember)
        viewModelScope.launch { repository.setEmail(value) }
    }

    private fun setPassword(value: String) {
        state = LoginState(state.email, value, state.remember)
        viewModelScope.launch { repository.setPassword(value) }
    }

    private fun setRemember(value: Boolean) {
        state = LoginState(state.email, state.password, value)
        viewModelScope.launch { repository.setRemember(value) }
    }

    init {
        viewModelScope.launch {
            state = LoginState(repository.email.first(), repository.password.first(), repository.remember.first())
        }
    }

    fun onLogin(
        email: String,
        password: String,
        remember: Boolean,
        navController: NavHostController
    ) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setEmail(email)
                    setRemember(remember)
                    setPassword(password)
                    navController.navigate(MemoryExplorerRoute.Home.route) {
                        popUpTo(MemoryExplorerRoute.Login.route) { inclusive = true }
                    }
                } else {
                    Toast.makeText(navController.context, R.string.auth_failed, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun onRegister(navController: NavHostController) {
        navController.navigate(MemoryExplorerRoute.Register.route)
    }

    fun checkEmailAndPassword(context: Context, navController: NavHostController) {
        viewModelScope.launch {
            if (repository.email.first().isNotEmpty() && repository.password.first().isNotEmpty()) {
                onLogin(repository.email.first(), repository.password.first(), repository.remember.first(),navController)
            }
            else {
                Toast.makeText(context, context.getString(R.string.must_load_with_account), Toast.LENGTH_LONG).show()
            }
        }
    }
}