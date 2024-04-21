package com.example.memoryexplorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.memoryexplorer.ui.screens.addmemory.AddMemoryScreen
import com.example.memoryexplorer.ui.screens.home.HomeScreen
import com.example.memoryexplorer.ui.screens.login.LoginScreen
import com.example.memoryexplorer.ui.screens.login.LoginViewModel
import com.example.memoryexplorer.ui.screens.profile.ProfileScreen
import com.example.memoryexplorer.ui.screens.register.RegisterScreen
import com.example.memoryexplorer.ui.screens.register.RegisterViewModel
import com.example.memoryexplorer.ui.screens.settings.SettingsScreen
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

    companion object {
        val routes = setOf(Home, Login, Register, AddMemory, Profile, Settings)
    }
}

@Composable
fun MemoryExplorerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
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
                SettingsScreen(navController)
            }
        }
    }
}
