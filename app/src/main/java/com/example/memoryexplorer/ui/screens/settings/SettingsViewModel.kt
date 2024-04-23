package com.example.memoryexplorer.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import kotlinx.coroutines.launch

data class SettingsState(
    val email: String,
    val remember: Boolean
)

class SettingsViewModel(
) : ViewModel() {
    fun onLogout(navController: NavHostController, loginRepository: LoginRepository) {
        viewModelScope.launch {
            loginRepository.logout()
            navController.navigate(MemoryExplorerRoute.Login.route) {
                popUpTo(MemoryExplorerRoute.Home.route) { inclusive = true }
            }
        }
    }
}