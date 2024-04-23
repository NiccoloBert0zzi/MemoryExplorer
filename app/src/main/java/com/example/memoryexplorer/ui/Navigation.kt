package com.example.memoryexplorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.screens.addmemory.AddMemoryScreen
import com.example.memoryexplorer.ui.screens.home.HomeScreen
import com.example.memoryexplorer.ui.screens.login.LoginScreen
import com.example.memoryexplorer.ui.screens.login.LoginViewModel
import com.example.memoryexplorer.ui.screens.profile.ProfileScreen
import com.example.memoryexplorer.ui.screens.register.RegisterScreen
import com.example.memoryexplorer.ui.screens.register.RegisterViewModel
import com.example.memoryexplorer.ui.screens.settings.SettingsScreen
import com.example.memoryexplorer.ui.screens.statistics.StatisticsScreen
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

sealed class MemoryExplorerRoute(
    val route: String,
    val title: String,
) {
    data object Home : MemoryExplorerRoute("home", "Memory Explorer")
    data object Login : MemoryExplorerRoute("login", "Login")
    data object Register : MemoryExplorerRoute("register", "Register")
    data object AddMemory : MemoryExplorerRoute("addMemory", "Add Memory")
    data object Profile : MemoryExplorerRoute("profile", "Profile")
    data object Settings : MemoryExplorerRoute("settings", "Settings")
    data object Statistics : MemoryExplorerRoute("statistics", "Statistics")

    companion object {
        val routes = setOf(Login, Home, Register, AddMemory, Profile, Settings, Statistics)
    }
}

@Composable
fun MemoryExplorerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
)  {
    val loginRepository = get<LoginRepository>() // Get LoginRepository instance using Koin
    val rememberMe by loginRepository.remember.collectAsState(initial = false) // Ottieni il valore corrente di REMEMBER_ME
    NavHost(
        navController = navController,
        startDestination = (if (rememberMe) MemoryExplorerRoute.Home else MemoryExplorerRoute.Login).route,
        modifier = modifier
    ) {
        with(MemoryExplorerRoute.Login) {
            composable(route) {
                val loginViewModel = koinViewModel<LoginViewModel>()
                LoginScreen(
                    navController,
                    loginViewModel::onLogin,
                    loginViewModel::onRegister
                )
            }
        }
        with(MemoryExplorerRoute.Home) {
            composable(route) {
                HomeScreen(navController)
            }
        }
        with(MemoryExplorerRoute.Register) {
            composable(route) {
                val registerViewModel = koinViewModel<RegisterViewModel>()
                RegisterScreen(
                    navController,
                    registerViewModel::onLogin,
                    registerViewModel::onRegister
                )
            }
        }
        with(MemoryExplorerRoute.AddMemory) {
            composable(route) {
                AddMemoryScreen(navController)
            }
        }
        with(MemoryExplorerRoute.Profile) {
            composable(route) {
                ProfileScreen(navController)
            }
        }
        with(MemoryExplorerRoute.Settings) {
            composable(route) {
                SettingsScreen(navController, loginRepository)
            }
        }
        with(MemoryExplorerRoute.Statistics) {
            composable(route) {
                StatisticsScreen(navController)
            }
        }
    }
}