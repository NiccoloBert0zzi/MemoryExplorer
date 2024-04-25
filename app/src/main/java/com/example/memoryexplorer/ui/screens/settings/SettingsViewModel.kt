package com.example.memoryexplorer.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    fun onLogout(
        navController: NavHostController,
        loginRepository: LoginRepository,
        favouriteRepository: FavouriteRepository
    ) {
        viewModelScope.launch {
            loginRepository.logout()
            favouriteRepository.logout()
            navController.navigate(MemoryExplorerRoute.Login.route) {
                popUpTo(MemoryExplorerRoute.Home.route) { inclusive = true }
            }
        }
    }

    fun onThemeChange(selectedTheme: String) {
        //TODO theme app
    }
}