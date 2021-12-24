package com.example.a05_photogallery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val  TAG = "NotificationReceiver"

class NotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Received broadcast: ${intent?.action}")
    }
}
