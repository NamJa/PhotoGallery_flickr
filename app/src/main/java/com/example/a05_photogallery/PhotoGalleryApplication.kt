package com.example.a05_photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

const val NOTIFICATION_CHANNEL_ID = "flickr_poll"

// 최상위단의 클래스 -> Application을 상속한다.
class PhotoGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 안드로이드 버전 8.0부터는 최소 1개의 channel을 구성하여 알림을 설정해야한다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}