package com.example.memoryexplorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.memoryexplorer.ui.screens.home.HomeScreen
import com.example.memoryexplorer.ui.screens.login.LoginScreen
import com.example.memoryexplorer.ui.screens.login.LoginViewModel
import org.koin.androidx.compose.koinViewModel

sealed class MemoryExplorerRoute(
    val route: String,
    val title: String,
) {
    data object Home : MemoryExplorerRoute("home", "Memory Explorer")
    data object Login : MemoryExplorerRoute("login", "Login")

    companion object {
        val routes = setOf(Home, Login)
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
                    loginViewModel::onLogin,
                    loginViewModel::onRegister
                )
            }
        }
    }
}
