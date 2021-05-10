package com.simpleplus.timecounter.application

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.database.TimeCounterDatabase
import com.simpleplus.timecounter.repository.EventRepository

class EventApplication : Application() {

    //Database
    private val database by lazy { TimeCounterDatabase.getInstance(this) }
    val eventRepo by lazy { EventRepository(database.EventDao()) }
}