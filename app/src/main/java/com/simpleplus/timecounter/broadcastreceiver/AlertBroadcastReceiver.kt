package com.simpleplus.timecounter.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlertBroadcastReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"deu",Toast.LENGTH_SHORT).show()
        Log.i("Porsche", "onReceive: deu")
    }
}
