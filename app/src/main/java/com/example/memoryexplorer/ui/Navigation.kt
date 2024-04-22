package com.example.memoryexplorer.ui

import androidx.compose.runtime.Composable
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
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

sealed class MemoryExplorerRoute(
    val route: String,
    val title: String,
) {
    object Home : MemoryExplorerRoute("home", "Memory Explorer")
    object Login : MemoryExplorerRoute("login", "Login")
    object Register : MemoryExplorerRoute("register", "Register")
    object AddMemory : MemoryExplorerRoute("addMemory", "Add Memory")
    object Profile : MemoryExplorerRoute("profile", "Profile")
    object Settings : MemoryExplorerRoute("settings", "Settings")

    companion object {
        val routes = setOf(Home, Login, Register, AddMemory, Profile, Settings)
    }
}

@Composable
fun MemoryExplorerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
)  {
    val loginRepository = get<LoginRepository>() // Get LoginRepository instance using Koin

    NavHost(
        navController = navController,
        startDestination = MemoryExplorerRoute.Home.route,
        modifier = modifier
    ) {
        with(MemoryExplorerRoute.Home) {
            composable(route) {
                HomeScreen(navController)
            }
        }
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
                SettingsScreen(navController,loginRepository)
            }
        }
    }
}