package com.example.memoryexplorer.ui.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerRoute

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    loginRepository: LoginRepository // Inject LoginRepository
) {
    val coroutineScope = rememberCoroutineScope()

    ClickableText(
        text = AnnotatedString("Logout", SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)),
        onClick = {
            coroutineScope.launch {
                // Clear user data
                loginRepository.logout()

                // Navigate to login screen
                navController.navigate(MemoryExplorerRoute.Login.route) {
                    popUpTo(MemoryExplorerRoute.Home.route) { inclusive = true }
                }
            }
        },
        modifier = Modifier.padding(top = 16.dp, start = 16.dp) // Add padding on top and start
    )
}