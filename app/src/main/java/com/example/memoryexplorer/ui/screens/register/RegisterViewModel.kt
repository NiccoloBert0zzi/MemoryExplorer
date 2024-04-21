package com.example.memoryexplorer.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute

data class RegisterState(
    val email: String,
    val remember: Boolean
)

class RegisterViewModel(
    private val repository: LoginRepository
) : ViewModel() {
    fun onLogin(navController: NavHostController) {
        navController.navigateUp()
    }

    fun onRegister(navController: NavHostController) {
        navController.navigate(MemoryExplorerRoute.Home.route) {
            popUpTo(MemoryExplorerRoute.Login.route) { inclusive = true }
        }
    }
}