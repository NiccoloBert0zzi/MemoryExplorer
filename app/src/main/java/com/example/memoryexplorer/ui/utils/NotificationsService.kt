package com.example.memoryexplorer.ui.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.memoryexplorer.R
import com.example.memoryexplorer.data.database.Memory
import com.google.firebase.database.FirebaseDatabase

class NotificationsService(
    private val context: Context,
    private val locationService: LocationService
) {
    private val memories = mutableListOf<Memory>()
    private var runnable: Runnable? = null
    private var cacheNotifications: MutableList<String> = ArrayList()

    fun init(email: String) {
        downloadMemories(email)
    }

    private fun sendNotification(title: String) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this.context, "notification")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(context.getString(R.string.near_at, title))
                .setContentText(context.getString(R.string.come_back_to_visit))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    private fun checkNotification() {
        val handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                handler.postDelayed(this, 10000)
                val results = FloatArray(1)
                if (memories.isNotEmpty()) {
                    for (memory in memories) {
                        Location.distanceBetween(
                            locationService.coordinates?.latitude ?: 0.0,
                            locationService.coordinates?.longitude ?: 0.0,
                            memory.latitude!!.toDouble(),
                            memory.longitude!!.toDouble(),
                            results
                        )
                        if (results[0] < 5000) {
                            println(
                                context.getString(R.string.you_are)
                                        + results[0]
                                        + context.getString(R.string.from_the)
                                        + memory.title
                            )
                            if (!cacheNotifications.contains(memory.id)) {
                                cacheNotifications.add(memory.id!!)
                                sendNotification(memory.title!!)
                            }
                        }
                    }
                }
            }
        }
        runnable!!.run()
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
        checkNotification()
    }
}