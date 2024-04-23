package com.example.memoryexplorer.ui.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.memoryexplorer.data.repositories.LoginRepository

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memoryexplorer.R


@Composable
fun SettingsScreen(
    navController: NavHostController,
    loginRepository: LoginRepository,
    onLogout: (NavHostController, LoginRepository) -> Unit
) {

    ClickableText(
        text = AnnotatedString(
            stringResource(R.string.logout),
            SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        ),
        onClick = { onLogout(navController, loginRepository) },
        modifier = Modifier.padding(top = 16.dp, start = 16.dp)
    )
}