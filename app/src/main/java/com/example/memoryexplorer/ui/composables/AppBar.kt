package com.example.memoryexplorer.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.memoryexplorer.ui.MemoryExplorerRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: MemoryExplorerRoute,
    title: String?
) {
    if (currentRoute.route == MemoryExplorerRoute.Login.route || currentRoute.route == MemoryExplorerRoute.Register.route) {
        return
    }
    val routeTitle = navController.context.getString(currentRoute.titleId)
    CenterAlignedTopAppBar(
        title = {
            Text(
                title ?: routeTitle,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
        },
        actions = {
            if (currentRoute.route == MemoryExplorerRoute.Home.route) {
                IconButton(onClick = {
                    navController.navigate(MemoryExplorerRoute.Profile.route)
                }) {
                    Icon(
                        Icons.Rounded.AccountCircle,
                        contentDescription = "User account"
                    )
                }
            }
            if (currentRoute.route == MemoryExplorerRoute.Profile.route) {
                IconButton(onClick = {
                    navController.navigate(MemoryExplorerRoute.Settings.route)
                }) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Settings icon"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    )
}
