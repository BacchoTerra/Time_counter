package com.simpleplus.timecounter.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.EventAdapter
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.broadcastreceiver.AlertBroadcastReceiver
import com.simpleplus.timecounter.databinding.ActivityMainBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.utils.ChipFilterHelper
import com.simpleplus.timecounter.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
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
    private lateinit var allEvent: LiveData<List<Event>>

    //ActivityResult
    private lateinit var addEventLauncher: ActivityResultLauncher<Intent>
    private lateinit var editEventLauncher: ActivityResultLauncher<Intent>

    //Recyclerview
    private lateinit var adapter: EventAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        initToolbar()
        startLaunchers()
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

    private fun initRecyclerView() {
        adapter = EventAdapter(editEventLauncher, this, eventViewModel)
        val recyclerView = binder.activityMainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        allEvent = eventViewModel.selectAll()

        submitListToAdapter()
        handleChipsFilter()
    }

    private fun submitListToAdapter() {

        allEvent.observe(this, {

            val currentTime = System.currentTimeMillis()

            for (event in it) {
                if (!event.isFinished && event.timestamp <= currentTime) {
                    eventViewModel.update(event.copy(isFinished = true))
                }
            }


            adapter.submitList(it)

        })

    }

    private fun handleChipsFilter() {

        val chipHelper = ChipFilterHelper(
            this,
            binder.activityMainChipGroupMonth,
            binder.activityMainChipGroupYear
        )

        chipHelper.dateSelectionListener = { month: Int, year: Int ->

            when {

                month == ChipFilterHelper.NO_SELECTION && year == ChipFilterHelper.NO_SELECTION -> {
                    allEvent = eventViewModel.selectAll()
                    submitListToAdapter()
                }

                month != ChipFilterHelper.NO_SELECTION && year == ChipFilterHelper.NO_SELECTION -> {
                    allEvent = eventViewModel.selectAllFromMonth(month)
                    submitListToAdapter()

                }

                month == ChipFilterHelper.NO_SELECTION && year != ChipFilterHelper.NO_SELECTION -> {

                    allEvent = eventViewModel.selectAllFromYear(year)
                    submitListToAdapter()

                }

                year == ChipFilterHelper.INFINITE_YEAR -> {

                    allEvent = eventViewModel.selectAllFromBeyond(chipHelper.beyondYears)
                    submitListToAdapter()

                }

                else -> {

                    allEvent = eventViewModel.selectAllFromMonthAndYear(month, year)
                    submitListToAdapter()

                }


            }


        }

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