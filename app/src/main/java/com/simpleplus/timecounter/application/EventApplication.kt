package com.simpleplus.timecounter.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.database.TimeCounterDatabase
import com.simpleplus.timecounter.repository.EventRepository

class EventApplication : Application() {

    companion object {
        const val chanel1Id = "NTC_CHANNEL_1"
    }

    //Database
    private val database by lazy { TimeCounterDatabase.getInstance(this) }
    val eventRepo by lazy { EventRepository(database.EventDao()) }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel1 = NotificationChannel(chanel1Id,getString(R.string.app_name),NotificationManager.IMPORTANCE_HIGH)
            channel1.enableVibration(true)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel1)


        }
    }

}