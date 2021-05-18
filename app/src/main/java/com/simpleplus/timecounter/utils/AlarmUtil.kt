package com.simpleplus.timecounter.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.simpleplus.timecounter.broadcastreceiver.AlertBroadcastReceiver
import com.simpleplus.timecounter.model.Event

class AlarmUtil(val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

     fun setAlarm(event: Event, lastId: Long) {

         if (event.timestamp < System.currentTimeMillis()) return

        val intent = Intent(context, AlertBroadcastReceiver::class.java)
        AlertBroadcastReceiver.event = event
        val pendingIntent = PendingIntent.getBroadcast(context, lastId.toInt(), intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.timestamp, pendingIntent)

    }

     fun cancelAlarm(event: Event) {

        val intent = Intent(context, AlertBroadcastReceiver::class.java)
        AlertBroadcastReceiver.event = event
        val pendingIntent = PendingIntent.getBroadcast(context, event.id, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

     fun updateAlarm(event: Event) {

        val intent = Intent(context, AlertBroadcastReceiver::class.java)
        AlertBroadcastReceiver.event = event
        val pendingIntent = PendingIntent.getBroadcast(context, event.id, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.timestamp, pendingIntent)

    }


}