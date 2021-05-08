package com.simpleplus.timecounter.application

import android.app.Application
import com.simpleplus.timecounter.database.TimeCounterDatabase
import com.simpleplus.timecounter.repository.EventRepository

class EventApplication:Application() {

    private val database by lazy { TimeCounterDatabase.getInstance(this) }
    val eventRepo by lazy { EventRepository(database.EventDao()) }

}