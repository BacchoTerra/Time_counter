package com.simpleplus.timecounter.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.EventAdapter
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.broadcastreceiver.AlertBroadcastReceiver
import com.simpleplus.timecounter.databinding.ActivityMainBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.viewmodel.EventViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder: ActivityMainBinding

    //ViewModel
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(
            (application as EventApplication).eventRepo
        )
    }

    //ActivityResult
    private lateinit var addEventLauncher: ActivityResultLauncher<Intent>
    private lateinit var editEventLauncher: ActivityResultLauncher<Intent>

    //Recyclerview
    private lateinit var adapter: EventAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        startLaunchers()
        startAddActivity()
        initRecyclerView()
    }

    private fun startLaunchers() {
        addEventLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val event: Event =
                        it.data?.extras?.getParcelable(getString(R.string.extra_key_event))!!

                    lifecycleScope.launch {
                        eventViewModel.insert(event).join()
                        if (event.isNotifying) setAlarm(event,eventViewModel.lastId)
                    }
                    Snackbar.make(binder.root, R.string.label_event_added, Snackbar.LENGTH_SHORT)
                        .show()

                }

            }

        editEventLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == RESULT_OK) {

                    val event: Event =
                        it.data?.extras?.getParcelable(getString(R.string.extra_key_event))!!

                    if (it.data?.extras?.getBoolean(getString(R.string.extra_key_delete_event))!!) {
                        eventViewModel.delete(event)
                        cancelAlarm(event)
                    } else {
                        eventViewModel.update(event)
                        updateAlarm(event)
                        Log.i("PorscheMain", "startLaunchers: ${event.id}")
                        Snackbar.make(
                            binder.root,
                            R.string.label_event_updated,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                }

            }
    }

    private fun startAddActivity() {

        binder.activityMainContentFabAdd.contentSurfaceAddFabAdd.setOnClickListener {

            val intent = Intent(this, AddEventActivity::class.java)
            addEventLauncher.launch(intent)

        }

    }

    private fun initRecyclerView() {
        adapter = EventAdapter(editEventLauncher, this, eventViewModel)
        val recyclerView = binder.activityMainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        eventViewModel.allEvents.observe(this, {


            autoUpdateEventsAndAdapter(it, adapter)
            countOnAndOffEvents(it)

            if (it.isNotEmpty()) binder.activityMainTxtNoItensToShow.visibility =
                View.GONE else binder.activityMainTxtNoItensToShow.visibility = View.VISIBLE

        })

    }

    private fun autoUpdateEventsAndAdapter(list: List<Event>, adapter: EventAdapter) {

        val currentTime = System.currentTimeMillis()

        for (event in list) {
            if (!event.isFinished && event.timestamp <= currentTime) {
                eventViewModel.update(event.copy(isFinished = true))
            }
        }

        adapter.submitList(list)

    }

    private fun countOnAndOffEvents(list: List<Event>) {

        var on = 0
        var off = 0

        for (e in list) {
            if (e.isFinished) off++ else on++
        }

        binder.activityMainTxtInactiveTimers.text = getString(R.string.label_inactive_timers,off)
        binder.activityMainTxtActiveTimers.text = getString(R.string.label_active_timers,on)

    }

    private fun setAlarm(event: Event,lastId:Long) {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertBroadcastReceiver::class.java)
        AlertBroadcastReceiver.event = event
        val pendingIntent = PendingIntent.getBroadcast(this, lastId.toInt(), intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.timestamp, pendingIntent)

    }

    private fun cancelAlarm(event: Event) {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertBroadcastReceiver::class.java)
        AlertBroadcastReceiver.event = event
        val pendingIntent = PendingIntent.getBroadcast(this, event.id, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    private fun updateAlarm(event:Event){

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertBroadcastReceiver::class.java)
        AlertBroadcastReceiver.event = event
        val pendingIntent = PendingIntent.getBroadcast(this, event.id, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.timestamp, pendingIntent)

    }
}