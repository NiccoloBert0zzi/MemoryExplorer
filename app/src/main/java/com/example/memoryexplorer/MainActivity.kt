package com.example.memoryexplorer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.memoryexplorer.data.database.Memory
import com.example.memoryexplorer.ui.MemoryExplorerNavGraph
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.example.memoryexplorer.ui.composables.AppBar
import com.example.memoryexplorer.ui.theme.MemoryExplorerTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memoryexplorer.data.models.Theme
import com.example.memoryexplorer.ui.screens.settings.SettingsViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {

    //var loginRepository: LoginRepository
    var email: String = ""
    private val memories = mutableListOf<Memory>()

    //notification variables
    private var runnable: Runnable? = null
    companion object {
        var latitude: Double = 0.0
        var longitude: Double = 0.0
    }
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
        .setWaitForAccurateLocation(false)
        .setMinUpdateIntervalMillis(3000)
        .setMaxUpdateDelayMillis(100)
        .build()
    var cacheNotifications: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCurrentLocation()
        checkNotification()
        lifecycleScope.launch {
            email = "p@p.com"
            downloadMemories(email)
            runnable!!.run()
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
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            MemoryExplorerRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: MemoryExplorerRoute.Login
                        }
                    }
                    Log.d("MainActivity", "currentRoute: $currentRoute")
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

    fun sendNotification(title: String, id: String) {
        //CLICK NOTIFICATION
//        val intent = Intent(this, MainActivity::class.java).apply {
//            putExtra("memoryId", id)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "notification")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(getString(R.string.near_at, title))
            .setContentText(getString(R.string.come_back_to_visit))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        //.setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var channel = notificationManager.getNotificationChannel("notification")
        if (channel == null) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel("notification", "notification_channel", importance)
            channel.lightColor = Color.GREEN
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, builder.build())
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //not have permission
            return
        } else {
            LocationServices.getFusedLocationProviderClient(this@MainActivity)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(this@MainActivity)
                            .removeLocationUpdates(this)
                        if (locationResult.locations.size > 0) {
                            val index = locationResult.locations.size - 1
                            latitude = locationResult.locations[index].latitude
                            longitude = locationResult.locations[index].longitude
                        }
                    }
                }, Looper.getMainLooper())
        }
    }

    private fun checkNotification() {
        val handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                //get position every 5 seconds
                handler.postDelayed(this, 5000)
                val results = FloatArray(1)
                getCurrentLocation()
                if (memories.isNotEmpty()) {
                    for (m in memories) {
                        Location.distanceBetween(
                            latitude,
                            longitude,
                            m.latitude!!.toDouble(),
                            m.longitude!!.toDouble(),
                            results
                        )
                        //distance < 5000m
                        if (results[0] < 5000) {
                            println(getString(R.string.you_are)
                                    + results[0]
                                    + getString(R.string.from_the)
                                    + m.title)
                            if (!cacheNotifications.contains(m.id)) {
                                cacheNotifications.add(m.id!!)
                                sendNotification(m.title!!, m.id!!)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun downloadMemories(email: String) {
        val database = FirebaseDatabase.getInstance().getReference("memories")
        database.get().addOnSuccessListener {
            val memoriesLoad = it.children.map { snapshot ->
                val memory = snapshot.getValue(Memory::class.java)
                memory
            }
            for (memory in memoriesLoad) {
                if (memory != null && memory.creator == email) {
                    memories.add(memory)
                }
            }
        }
    }

}
