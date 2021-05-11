package com.simpleplus.timecounter.broadcastreceiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.model.Event

class AlertBroadcastReceiver:BroadcastReceiver() {

    companion object {
        var event:Event? = null
    }

    private lateinit var notificationManager:NotificationManagerCompat

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"deu",Toast.LENGTH_SHORT).show()
        Log.i("Porsche", "onReceive: deu")

        notificationManager = NotificationManagerCompat.from(context!!)

        val notification = NotificationCompat.Builder(context,EventApplication.chanel1Id).apply {
            setContentTitle(event?.eventName)
            setContentText("Chegou a hora")
            setSmallIcon(R.drawable.ic_baseline_check_circle_24)
        }.build()

        notificationManager.notify(1,notification)

    }
}
