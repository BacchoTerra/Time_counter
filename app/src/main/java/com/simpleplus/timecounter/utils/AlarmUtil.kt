package com.simpleplus.timecounter.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.broadcastreceiver.AlertBroadcastReceiver
import com.simpleplus.timecounter.model.Event

class AlarmUtil(val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Sets an alarm using event's information
     *
     * @param event the event to set the alarm for
     * @param lastId the id of this event, used if the application is running, to get the events name to create the notification
     */

     fun setAlarm(event: Event, lastId: Long) {

         if (event.timestamp < System.currentTimeMillis()) return

        val intent = Intent(context, AlertBroadcastReceiver::class.java)
        intent.putExtra(context.getString(R.string.extra_key_to_notification_receiver),createBundleWithEvent(event))
        val pendingIntent = PendingIntent.getBroadcast(context, lastId.toInt(), intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.timestamp, pendingIntent)

    }
    /**
     * Cancels the alarm for an event
     *
     * @param event to cancel alarm for
     */
     fun cancelAlarm(event: Event) {

        val intent = Intent(context, AlertBroadcastReceiver::class.java)
        intent.putExtra(context.getString(R.string.extra_key_to_notification_receiver),createBundleWithEvent(event))
        val pendingIntent = PendingIntent.getBroadcast(context, event.id, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    /**
     * Method to update an alarm if an event is updated, it first delete the old one, and then it updates
     *
     * @param event the updated event
     */
     fun updateAlarm(event: Event) {

       cancelAlarm(event)

        val intent = Intent(context, AlertBroadcastReceiver::class.java)
        intent.putExtra(context.getString(R.string.extra_key_to_notification_receiver),createBundleWithEvent(event))
        val pendingIntent = PendingIntent.getBroadcast(context, event.id, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.timestamp, pendingIntent)

    }

    private fun createBundleWithEvent(event: Event) : Bundle {

        val bundle = Bundle()
        bundle.putParcelable(context.getString(R.string.extra_key_to_notification_receiver),event)

        return bundle
    }

}