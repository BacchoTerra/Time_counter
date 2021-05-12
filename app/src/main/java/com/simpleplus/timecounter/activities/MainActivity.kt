package com.simpleplus.timecounter.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import com.simpleplus.timecounter.utils.DateFilterHelper
import com.simpleplus.timecounter.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.util.*

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

    //DateFilter calendar
    val calendarDateFilter = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        initToolbar()
        startLaunchers()
        buildDateFilterRecyclerView()
        initRecyclerView()
        updateEventIfAppIsRunning()
    }

    private fun initToolbar() {

        setSupportActionBar(binder.activityMainToolbar)

    }

    private fun startLaunchers() {
        addEventLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val event: Event =
                        it.data?.extras?.getParcelable(getString(R.string.extra_key_event))!!

                    lifecycleScope.launch {
                        eventViewModel.insert(event).join()
                        AlertBroadcastReceiver.lastEventId = eventViewModel.lastId
                        if (event.isNotifying) setAlarm(event, eventViewModel.lastId)
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

    private fun buildDateFilterRecyclerView() {

        val helper = DateFilterHelper(this, binder.activityMainDateFilterRecyclerView,getScreenWidth())
        helper.buildRecyclerView(2021)
    }

    private fun getScreenWidth(): Int {

        var screenWidth = 0
        val displayMetric = DisplayMetrics()

        screenWidth = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {

            windowManager.defaultDisplay.getMetrics(displayMetric)
            displayMetric.widthPixels
        } else {
            this.display?.getRealMetrics(displayMetric)
            displayMetric.widthPixels
        }

        Log.i("Porsche", "getScreenWidth: $screenWidth")
        Log.i("Porsche", "getScreenWidth: ${screenWidth / 2}")
        Log.i("Porsche", "getScreenWidth: ${screenWidth /2 - screenWidth/2}")
        return screenWidth

    }

    private fun initRecyclerView() {
        adapter = EventAdapter(editEventLauncher, this, eventViewModel)
        val recyclerView = binder.activityMainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        eventViewModel.allEvents.observe(this, {
            autoUpdateEventsAndAdapter(it, adapter)
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

    private fun setAlarm(event: Event, lastId: Long) {

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

    private fun updateAlarm(event: Event) {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertBroadcastReceiver::class.java)
        AlertBroadcastReceiver.event = event
        val pendingIntent = PendingIntent.getBroadcast(this, event.id, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.timestamp, pendingIntent)

    }

    private fun updateEventIfAppIsRunning() {

        AlertBroadcastReceiver.listener = {

            eventViewModel.update(it.copy(isFinished = true))

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.main_menu_add -> addEventLauncher.launch(
                Intent(
                    this,
                    AddEventActivity::class.java
                )
            )

            R.id.main_menu_info -> {
                Log.i("Simple porsche", "onOptionsItemSelected: info")
            }

        }

        return true
    }

}