package com.simpleplus.timecounter.broadcastreceiver

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.activities.MainActivity
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.model.Event

class AlertBroadcastReceiver : BroadcastReceiver() {

    companion object {
        var event: Event? = null
        var lastEventId = 0L
        var listener: ((Event) -> Unit)? = null
    }


    private lateinit var notificationManager: NotificationManagerCompat

    override fun onReceive(context: Context?, intent: Intent?) {
        createNotification(context)


    }

    private fun createNotification(context: Context?) {
        notificationManager = NotificationManagerCompat.from(context!!)
        val intent = Intent(context,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,1,intent,0)

        val notification = NotificationCompat.Builder(context, EventApplication.chanel1Id).apply {
            priority = NotificationCompat.PRIORITY_MAX
            setDefaults(Notification.DEFAULT_ALL)
            setContentTitle(event?.eventName)
            setContentText(context.getString(R.string.notification_content))
            setSmallIcon(R.drawable.ic_baseline_check_circle_24)
            setContentIntent(pendingIntent)
        }.build()

        notificationManager.notify(1, notification)

        listener?.invoke(event?.copy(id = lastEventId.toInt())!!)

    }
}
