package ars.example.musicplayerapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NewApp:Application() {


    override fun onCreate() {
        val managerNotification  = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel  = NotificationChannel(Constants.CHANNEL_ID,getString(R.string.CHANNEL_NAME)
                ,NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = getString(R.string.CHANNEL_DESCRIPTION)
            }
            managerNotification.createNotificationChannel(channel)
        }
        super.onCreate()
    }
}