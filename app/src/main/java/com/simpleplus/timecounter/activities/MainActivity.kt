package com.simpleplus.timecounter.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.EventAdapter
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.broadcastreceiver.AlertBroadcastReceiver
import com.simpleplus.timecounter.databinding.ActivityMainBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.utils.AlarmUtil
import com.simpleplus.timecounter.utils.ChipFilterHelper
import com.simpleplus.timecounter.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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

    //Recyclerview
    private lateinit var adapter: EventAdapter

    //Alarm util
    private lateinit var alarmUtil: AlarmUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)


        alarmUtil = AlarmUtil((this))
        initToolbar()
        displayHeaderClock()
        startLaunchers()
        initRecyclerView()
        updateEventIfAppIsRunning()

    }

    private fun initToolbar() {

        setSupportActionBar(binder.activityMainToolbar)
        supportActionBar?.title = null
    }

    private fun displayHeaderClock() {

        val calendar = Calendar.getInstance()

        val sdf = SimpleDateFormat("kk:mm", Locale.getDefault())
        binder.activityMainTxtDisplayHour.text = sdf.format(System.currentTimeMillis())

        val weekDayDN =
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val monthDN = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        binder.activityMainTxtDisplayDate.text =
            getString(R.string.label_week_day_month, weekDayDN, dayOfMonth, monthDN)


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
                        if (event.isNotifying) alarmUtil.setAlarm(event, eventViewModel.lastId)
                    }
                    Toast.makeText(this, R.string.label_event_added, Toast.LENGTH_SHORT).show()

                }

            }
    }

    private fun initRecyclerView() {
        adapter = EventAdapter(this, eventViewModel)
        val recyclerView = binder.activityMainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        allEvent = eventViewModel.selectAll()

        val animator: ItemAnimator = recyclerView.itemAnimator!!
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        submitListToAdapter()
        handleChipsFilter()

        adapter.switchListener = { b: Boolean, event: Event ->

            if (b) alarmUtil.setAlarm(event, event.id.toLong()) else alarmUtil.cancelAlarm(event)

        }

    }

    private fun submitListToAdapter() {

        allEvent.observe(this, {
            val currentTime = System.currentTimeMillis()

            for (event in it) {
                if (!event.isFinished && event.timestamp <= currentTime) {
                    eventViewModel.update(event.copy(isFinished = true))
                }
            }

            if (it.isEmpty()) binder.activityMainTxtNoItem.visibility =
                View.VISIBLE else binder.activityMainTxtNoItem.visibility = View.GONE


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
                    allEvent.removeObservers(this)
                    allEvent = eventViewModel.selectAll()
                    Log.i("Porsche", "handleChipsFilter: NO SELECTION")
                    submitListToAdapter()
                }

                month != ChipFilterHelper.NO_SELECTION && year == ChipFilterHelper.NO_SELECTION -> {
                    allEvent.removeObservers(this)
                    allEvent = eventViewModel.selectAllFromMonth(month)
                    Log.i("Porsche", "handleChipsFilter: MONTH")
                    submitListToAdapter()

                }

                month == ChipFilterHelper.NO_SELECTION && year != ChipFilterHelper.NO_SELECTION && year != ChipFilterHelper.INFINITE_YEAR -> {

                    allEvent.removeObservers(this)
                    allEvent = eventViewModel.selectAllFromYear(year)
                    Log.i("Porsche", "handleChipsFilter: YEAR")
                    submitListToAdapter()

                }

                year == ChipFilterHelper.INFINITE_YEAR -> {
                    allEvent.removeObservers(this)
                    allEvent = eventViewModel.selectAllFromBeyond(chipHelper.beyondYears)
                    Log.i("Porsche", "handleChipsFilter: INFINITE YEAR")
                    submitListToAdapter()

                }

                else -> {
                    allEvent.removeObservers(this)
                    allEvent = eventViewModel.selectAllFromMonthAndYear(month, year)
                    Log.i("Porsche", "handleChipsFilter: ELSE")
                    submitListToAdapter()

                }


            }


        }

    }

    private fun updateEventIfAppIsRunning() {

        AlertBroadcastReceiver.listener = {

            eventViewModel.update(it.copy(isFinished = true))

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_main_activity, menu)
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
                startActivity(Intent(this, AboutActivity::class.java))
            }

        }

        return true
    }

}