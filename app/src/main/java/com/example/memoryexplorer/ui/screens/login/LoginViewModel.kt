package com.example.memoryexplorer.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LoginState(
    val email: String,
    val remember: Boolean
)

class LoginViewModel (
    private val repository: LoginRepository
) : ViewModel() {
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

    fun onLogin(email: String, remember: Boolean, navController: NavHostController) {
        setEmail(email)
        setRemember(remember)
        navController.navigate(MemoryExplorerRoute.Home.route) {
            popUpTo(MemoryExplorerRoute.Login.route) { inclusive = true }
        }
    }

    fun onRegister(navController: NavHostController) {
        navController.navigate(MemoryExplorerRoute.Register.route)
    }
}