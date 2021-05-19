package com.simpleplus.timecounter.activities

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.databinding.ActivityInformationBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.utils.AlarmUtil
import com.simpleplus.timecounter.viewmodel.EventViewModel
import java.lang.IllegalArgumentException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class InformationActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder: ActivityInformationBinding

    //Event
    private lateinit var event: Event

    //formatter
    private val sdf = SimpleDateFormat("dd/MM/yyyy (HH:mm)", Locale.getDefault())
    private val numberFormatter: NumberFormat = NumberFormat.getNumberInstance()

    //ViewModel
    private val viewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory((application as EventApplication).eventRepo)
    }

    //Alarm util
    private lateinit var alarmUtil :AlarmUtil

    //Activity contract
    private val editLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == RESULT_OK) {

                event =
                    it.data?.extras?.get(getString(R.string.extra_key_event)) as Event

                viewModel.update(event)
                alarmUtil.updateAlarm(event)
                bindHeaderEventDetails()
                bindTimePeriods()
                formatAndSetRemainingTime(event.timestamp - System.currentTimeMillis())
                Toast.makeText(this,R.string.label_event_updated,Toast.LENGTH_SHORT).show()

            }


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binder.root)

        alarmUtil = AlarmUtil(this)
        retrieveEvent()
        bindHeaderEventDetails()
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        bindTimePeriods()
        formatAndSetRemainingTime(event.timestamp - System.currentTimeMillis())
    }

    /**
     * configures initial properties of this activity toolbar
     */
    private fun initToolbar() {

        setSupportActionBar(binder.activityInformationToolbar)
        supportActionBar?.let {
            title = null
            it.setDisplayHomeAsUpEnabled(true)
        }

    }

    /**
     * Retrieves the event passed to this activity by EventAdapter
     */
    private fun retrieveEvent() {

        event = intent?.extras?.get(getString(R.string.extra_key_event)) as Event

    }

    /**
     * Bind header values with events details
     */
    private fun bindHeaderEventDetails() {

        binder.activityInformationTxtEventName.text = event.eventName
        binder.activityInformationTxtExpectedDate.text = sdf.format(event.timestamp)

    }

    /**
     * Generates each time period (years,months,days,hours,minutes) to bind to each layout components
     * representing each one
     */
    private fun bindTimePeriods() {

        val timeStamp = event.timestamp - System.currentTimeMillis()

        //Setting days

        val days: Long = TimeUnit.MILLISECONDS.toDays(timeStamp)
        binder.activityInformationContentDaysMissing.contentTimeMissingTxtNumber.text =
            numberFormatter.format(days)
        binder.activityInformationContentDaysMissing.contentTimeMissingTxtPeriod.text =
            getString(R.string.label_period_missing,getString(R.string.label_day))

        //Setting hours
        binder.activityInformationContentHoursMissing.contentTimeMissingTxtNumber.text =
            numberFormatter.format(TimeUnit.MILLISECONDS.toHours(timeStamp))
        binder.activityInformationContentHoursMissing.contentTimeMissingTxtPeriod.text =
            getString(R.string.label_period_missing,getString(R.string.label_hours))

        //Setting minutes
        if (days < 1) {
            binder.activityInformationContentMinutesMissing.contentTimeMissingTxtNumber.text =
                numberFormatter.format(TimeUnit.MILLISECONDS.toMinutes(timeStamp))
            binder.activityInformationContentMinutesMissing.contentTimeMissingTxtPeriod.text =
                getString(R.string.label_period_missing,getString(R.string.label_minutes))
        } else {
            binder.activityInformationContentMinutesMissing.root.visibility = View.GONE
        }


        //Setting months
        val months = days / 30
        binder.activityInformationContentMonthsMissing.contentTimeMissingTxtNumber.text =
            numberFormatter.format(months)
        binder.activityInformationContentMonthsMissing.contentTimeMissingTxtPeriod.text =
            getString(R.string.label_period_missing,getString(R.string.label_months))
        //Setting years
        val years: Double = days / 365.0
        binder.activityInformationContentYearsMissing.contentTimeMissingTxtNumber.text =
            String.format("%.1f", years)
        binder.activityInformationContentYearsMissing.contentTimeMissingTxtPeriod.text =
            getString(R.string.label_period_missing,getString(R.string.label_years))

        if (timeStamp <= 0) {
            Toast.makeText(this,getString(R.string.toast_event_over,event.eventName),Toast.LENGTH_LONG).show()
            unregisterReceiver(tikReceiver)
        }

    }

    /**
     * Constructs and show an alert dialog to delete the current event
     */
    private fun initDeleteDialog() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(R.string.label_delete_event)
            setMessage(R.string.label_permanent_action)
            setPositiveButton(R.string.label_delete) { _: DialogInterface, _: Int ->

                viewModel.delete(event)
                alarmUtil.cancelAlarm(event)
                finish()

            }
            setNegativeButton(R.string.label_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()

    }

    /**
     * Opens AddEventActivity to edit current event
     */
    private fun launchEditingActivity() {

        editLauncher.launch(
            Intent(
                this,
                AddEventActivity::class.java
            ).apply { putExtra(getString(R.string.extra_key_event), event) })

    }

    /**
     * Generates numbers for showing how much time is left for the event
     *
     * @see InformationActivity.buildTimeString
     * @param millisToIt how much time missing to the event counting from current moment
     */
    private fun formatAndSetRemainingTime(millisToIt: Long) {

        val days = TimeUnit.MILLISECONDS.toDays(millisToIt)
        val hours = TimeUnit.MILLISECONDS.toHours(millisToIt) - TimeUnit.DAYS.toHours(
            TimeUnit.MILLISECONDS.toDays(millisToIt)
        )
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisToIt) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millisToIt)
        )

        binder.activityInformationTxtRemainingTime.text = buildTimeString(days, hours, minutes)
    }

    /**
     * Generates a string representing the time left to the event
     *
     * @see InformationActivity.formatAndSetRemainingTime
     * @param days how many days to it
     * @param hours how many hours to it
     * @param minutes how many minutesto it
     * @return a formatted string with the total time left to the event
     */
    private fun buildTimeString(days: Long, hours: Long, minutes: Long): String {

        val builder = StringBuilder()

        builder.append(if (days > 9) "${days}d " else "0${days}d ")
        builder.append(if (hours > 9) "${hours}h " else "0${hours}h ")
        builder.append(if (minutes > 9) "${minutes}m " else "0${minutes} m ")

        return builder.toString()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_toolbar_information_activity, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_information_menu_edit -> launchEditingActivity()

            R.id.menu_information_menu_delete -> initDeleteDialog()

        }


        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)
        registerReceiver(tikReceiver,intentFilter)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(tikReceiver)
        }catch (e:IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    /**
     * An object that receives an anonymous implementation of BroadcastReceiver
     * to handle TimeTik Events
     */
    private val tikReceiver = object:BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            bindTimePeriods()
            formatAndSetRemainingTime(event.timestamp - System.currentTimeMillis())
        }
    }
}