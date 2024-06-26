package com.example.memoryexplorer.ui.screens.settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.models.Theme
import java.text.Collator
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition", "DiscouragedApi")
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val themeState by settingsViewModel.state.collectAsState()
    val systemLanguage = Locale(Locale.getDefault().language)
        .getDisplayLanguage(Locale(Locale.getDefault().language)).lowercase()
    val languages = R.string::class.java.fields
        .filter { it.name.startsWith("language_") }
        .map {
            Pair(
                first = LocalContext.current.getString(it.getInt(it)),
                second = it.name.removePrefix("language_")
            )
        }
        .sortedWith(compareBy(Collator.getInstance(Locale("it", "IT"))) { it.first })

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
                    .padding(vertical = 4.dp),
            ) {
                Text(
                    stringResource(R.string.select_theme),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Theme.entries.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 8.dp)
                        .selectable(
                            selected = (theme == themeState.theme),
                            onClick = { settingsViewModel.changeTheme(theme) },
                            role = Role.RadioButton
                        ),
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
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    stringResource(R.string.available_languages),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            val context = LocalContext.current
            languages.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .let { modifier ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                modifier.clickable { settingsViewModel.changeLanguage(item.second, context) }
                            } else {
                                modifier
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            id = navController.context.resources.getIdentifier(
                                "flag_${item.second}",
                                "drawable",
                                navController.context.packageName
                            )
                        ),
                        contentDescription = "Flag icon",
                        modifier = Modifier
                            .width(68.dp)
                            .height(34.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        item.first,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (item.first.lowercase() == systemLanguage) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(top = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Button(
                    onClick = { settingsViewModel.onLogout(navController) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    }
}
