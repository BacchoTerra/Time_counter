package com.simpleplus.timecounter.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.model.Event

class AlertBroadcastReceiver:BroadcastReceiver() {

    companion object {
        var event:Event? = null
        var lastEventId = 0L
        var listener : ((Event) -> Unit)? = null
    }


    private lateinit var notificationManager:NotificationManagerCompat

    override fun onReceive(context: Context?, intent: Intent?) {
        createNotification(context)


    }

    private fun createNotification(context: Context?) {
        notificationManager = NotificationManagerCompat.from(context!!)

        val notification = NotificationCompat.Builder(context,EventApplication.chanel1Id).apply {
            setContentTitle(event?.eventName)
            Log.i("Porsche", "createNotification: ${event?.id}")
            setContentText("Chegou a hora")
            setSmallIcon(R.drawable.ic_baseline_check_circle_24)
        }.build()

        notificationManager.notify(1,notification)

        listener?.invoke(event?.copy(id = lastEventId.toInt())!!)

    }
}
