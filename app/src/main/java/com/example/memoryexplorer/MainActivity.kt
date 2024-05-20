package com.example.memoryexplorer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.memoryexplorer.ui.MemoryExplorerNavGraph
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.example.memoryexplorer.ui.composables.AppBar
import com.example.memoryexplorer.ui.theme.MemoryExplorerTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.memoryexplorer.data.models.Theme
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.screens.settings.SettingsViewModel
import com.example.memoryexplorer.ui.utils.CheckLocationPermission
import com.example.memoryexplorer.ui.utils.LocationService
import com.example.memoryexplorer.ui.utils.NotificationsService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StaticFieldLeak")
private lateinit var locationService: LocationService

class MainActivity : FragmentActivity() {
    private lateinit var notificationsService: NotificationsService
    private val loginRepository: LoginRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationService = LocationService(this)
        notificationsService = NotificationsService(this, locationService)

        lifecycleScope.launch {
            notificationsService.init(loginRepository.email.first())
        }

        setContent {
            val navController = rememberNavController()
            val settingsViewModel = koinViewModel<SettingsViewModel>()
            val themeState by settingsViewModel.state.collectAsStateWithLifecycle()

            MemoryExplorerTheme(
                darkTheme = when (themeState.theme) {
                    Theme.System -> isSystemInDarkTheme()
                    Theme.Light -> false
                    Theme.Dark -> true
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CheckLocationPermission(this, locationService)
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            MemoryExplorerRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: MemoryExplorerRoute.Login
                        }
                    }
                    Scaffold(
                        topBar = { AppBar(navController, currentRoute, null) }
                    ) { contentPadding ->
                        MemoryExplorerNavGraph(
                            navController,
                            modifier = Modifier.padding(contentPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationService.pauseLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        locationService.resumeLocationRequest()
    }

}

fun getLocationService(): LocationService {
    return locationService
}