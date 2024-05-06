@file:Suppress("DEPRECATION")

package com.example.memoryexplorer.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memoryexplorer.MainActivity
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.models.Theme
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition", "DiscouragedApi")
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val themeState by settingsViewModel.state.collectAsState()

    val language = R.string::class.java.fields
        .filter { it.name.startsWith("language_") }
        .mapNotNull { it.getInt(it) }
        .map { navController.context.getString(it) }
        .toTypedArray()

    val context = LocalContext.current
    val resources = context.resources

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            ) {
                Text(stringResource(R.string.select_theme))
            }
            Theme.entries.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (theme == themeState.theme),
                            onClick = { settingsViewModel.changeTheme(theme) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (theme == themeState.theme), onClick = null)
                    Text(
                        stringResource(
                            when (theme) {
                                Theme.System -> R.string.theme_system
                                Theme.Light -> R.string.theme_light
                                Theme.Dark -> R.string.theme_dark
                            }
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(stringResource(R.string.select_language))
            }
            language.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                ) {
                    Button(
                        onClick = {
                            // Change the locale of your app to English
                            val locale = Locale("en")
                            Locale.setDefault(locale)
                            val config = resources.configuration
                            config.setLocale(locale)
                            resources.updateConfiguration(config, resources.displayMetrics)

                            // Restart the app
                            val intent = Intent(context, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(
                                id = navController.context.resources.getIdentifier(
                                    "flag_${item.lowercase()}",
                                    "drawable",
                                    navController.context.packageName
                                )
                            ),
                            contentDescription = "Flag icon",
                            modifier = Modifier
                                .width(75.dp)
                                .height(38.dp),
                            tint = Color.Unspecified
                        )
                        Text(item)
                    }
                }
            }
            Divider(color = Color.Gray, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = { settingsViewModel.onLogout(navController) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    }
}
