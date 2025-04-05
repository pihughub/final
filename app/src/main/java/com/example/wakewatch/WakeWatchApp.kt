package com.example.wakewatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class WakeWatchApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Python when the application starts
        if (!Python.isStarted()) {
            try {
                Python.start(AndroidPlatform(this))
            } catch (e: Exception) {
                // Log error if needed, but keep app running
            }
        }

        // Create notification channel for the foreground service
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "fatigue_detection_service",
                "Fatigue Detection Service",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Used for the fatigue detection background service"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}