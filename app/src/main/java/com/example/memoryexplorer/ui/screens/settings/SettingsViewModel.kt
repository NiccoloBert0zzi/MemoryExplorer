package com.example.memoryexplorer.ui.screens.settings

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.models.Theme
import com.example.memoryexplorer.data.repositories.FavouriteRepository
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.data.repositories.ThemeRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.os.LocaleList


data class ThemeState(val theme: Theme)

class SettingsViewModel(
    private val loginRepository: LoginRepository,
    private val favouriteRepository: FavouriteRepository,
    private val themeRepository: ThemeRepository
) : ViewModel() {
    val state = themeRepository.theme.map { ThemeState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System)
    )

    fun changeTheme(theme: Theme) = viewModelScope.launch {
        themeRepository.setTheme(theme)
    }

    fun changeLanguage(language: String, context: Context) = viewModelScope.launch {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(
                    when (language) {
                        "ita" -> "it"
                        "fra" -> "fr"
                        "esp" -> "es"
                        else -> "en"
                    }
                )
        }
    }

    fun onLogout(navController: NavHostController) {
        viewModelScope.launch {
            loginRepository.logout()
            favouriteRepository.logout()
            navController.navigate(MemoryExplorerRoute.Login.route) {
                popUpTo(MemoryExplorerRoute.Home.route) { inclusive = true }
            }
        }
    }
}