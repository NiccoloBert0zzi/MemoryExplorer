@file:Suppress("DEPRECATION")

package com.example.memoryexplorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.screens.addmemory.AddMemoryScreen
import com.example.memoryexplorer.ui.screens.addmemory.AddMemoryViewModel
import com.example.memoryexplorer.ui.screens.home.HomeScreen
import com.example.memoryexplorer.ui.screens.home.HomeViewModel
import com.example.memoryexplorer.ui.screens.login.LoginScreen
import com.example.memoryexplorer.ui.screens.login.LoginViewModel
import com.example.memoryexplorer.ui.screens.memorydetails.MemoryDetailsScreen
import com.example.memoryexplorer.ui.screens.memorydetails.MemoryDetailsViewModel
import com.example.memoryexplorer.ui.screens.profile.ProfileScreen
import com.example.memoryexplorer.ui.screens.profile.ProfileViewModel
import com.example.memoryexplorer.ui.screens.register.RegisterScreen
import com.example.memoryexplorer.ui.screens.register.RegisterViewModel
import com.example.memoryexplorer.ui.screens.settings.SettingsScreen
import com.example.memoryexplorer.ui.screens.settings.SettingsViewModel
import com.example.memoryexplorer.ui.screens.statistics.StatisticsScreen
import com.example.memoryexplorer.ui.screens.statistics.StatisticsViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

sealed class MemoryExplorerRoute(
    var route: String,
    val titleId: Int,
) {
    data object Home : MemoryExplorerRoute("home", R.string.app_name)
    data object Login : MemoryExplorerRoute("login", R.string.login_page)
    data object Register : MemoryExplorerRoute("register", R.string.register_page)
    data object MemoryDetails : MemoryExplorerRoute("memoryDetails", R.string.memory_details_page)
    data object AddMemory : MemoryExplorerRoute("addMemory", R.string.add_memory_page)
    data object Profile : MemoryExplorerRoute("profile", R.string.profile_page)
    data object Settings : MemoryExplorerRoute("settings", R.string.settings_page)
    data object Statistics : MemoryExplorerRoute("statistics", R.string.statistics_page)

    companion object {
        val routes =
            setOf(Login, Home, Register, MemoryDetails, AddMemory, Profile, Settings, Statistics)
    }
}

@Composable
fun MemoryExplorerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val loginRepository = get<LoginRepository>()
    val rememberMe by loginRepository.remember.collectAsState(initial = false)
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
                    loginViewModel
                )
            }
        }
        with(MemoryExplorerRoute.Home) {
            composable(route) {
                val homeViewModel = koinViewModel<HomeViewModel>()
                HomeScreen(
                    navController,
                    homeViewModel
                )
            }
        }
        with(MemoryExplorerRoute.Register) {
            composable(route) {
                val registerViewModel = koinViewModel<RegisterViewModel>()
                RegisterScreen(
                    navController,
                    registerViewModel
                )
            }
        }
        with(MemoryExplorerRoute.MemoryDetails) {
            composable("$route/{memoryId}") { backStackEntry ->
                val memoryDetailsViewModel = koinViewModel<MemoryDetailsViewModel>()
                MemoryDetailsScreen(
                    navController,
                    backStackEntry.arguments?.getString("memoryId"),
                    memoryDetailsViewModel
                )
            }
        }
        with(MemoryExplorerRoute.AddMemory) {
            composable(route) {
                val addMemoryViewModel = koinViewModel<AddMemoryViewModel>()
                AddMemoryScreen(
                    navController,
                    addMemoryViewModel
                )
            }
        }
        with(MemoryExplorerRoute.Profile) {
            composable(route) {
                val profileViewModel = koinViewModel<ProfileViewModel>()
                ProfileScreen(
                    navController,
                    profileViewModel
                )
            }
        }
        with(MemoryExplorerRoute.Settings) {
            composable(route) {
                val settingsViewModel = koinViewModel<SettingsViewModel>()
                SettingsScreen(
                    navController,
                    settingsViewModel
                )
            }
        }
        with(MemoryExplorerRoute.Statistics) {
            composable(route) {
                val statisticsViewModel = koinViewModel<StatisticsViewModel>()
                StatisticsScreen(
                    navController,
                    statisticsViewModel
                )
            }
        }
    }
}