package com.simpleplus.timecounter.broadcastreceiver

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.activities.MainActivity
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.model.Event

class AlertBroadcastReceiver : BroadcastReceiver() {

    companion object {
        var lastEventId = 0L
        var listener: ((Event) -> Unit)? = null
    }


    private lateinit var notificationManager: NotificationManagerCompat

    override fun onReceive(context: Context?, intent: Intent?) {


        val bundle =
            intent?.extras?.getBundle(context?.getString(R.string.extra_key_to_notification_receiver))
        val event =
            bundle?.getParcelable(context?.getString(R.string.extra_key_to_notification_receiver)) as Event?

        createNotification(context, event!!)

    }

    private fun createNotification(context: Context?, event: Event) {
        notificationManager = NotificationManagerCompat.from(context!!)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, event.id, intent, 0)

        val notification = NotificationCompat.Builder(context, EventApplication.chanel1Id).apply {
            priority = NotificationCompat.PRIORITY_MAX
            setDefaults(Notification.DEFAULT_ALL)
            setContentTitle(event.eventName)
            setContentText(context.getString(R.string.notification_content))
            setSmallIcon(R.drawable.ic_baseline_check_circle_24)
            setContentIntent(pendingIntent)
        }.build()
        notificationManager.notify(event.id, notification)



        listener?.invoke(event.copy(id = lastEventId.toInt()))

    }
}
