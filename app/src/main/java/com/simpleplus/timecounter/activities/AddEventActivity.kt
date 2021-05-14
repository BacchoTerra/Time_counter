package com.simpleplus.timecounter.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.databinding.ActivityAddEditEventBinding
import com.simpleplus.timecounter.model.Event
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder: ActivityAddEditEventBinding

    //Event for editing
    private var eventEditing: Event? = null
    private var isEditing = false

    //Timestamp from materialDatePicker
    private val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY,0)
        set(Calendar.MINUTE,0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityAddEditEventBinding.inflate(layoutInflater)
        setContentView(binder.root)

        retrieveEventIfEditing()
        handlePickersVisibility()
        customizePickers()
        updateUiWithCalendarDate()


        binder.activityAddEventTxtSave.setOnClickListener {

            if (binder.activityAddEventEditEventTitle.text.isEmpty()){
                binder.activityAddEventEditEventTitle.error = "*"
            }else{
                createEvent()
            }

        }


    }

    private fun handlePickersVisibility() {
        binder.activityAddEventCardDate.setOnClickListener {


            changePickersVisibility(
                binder.activityAddEventDatePicker,
                binder.activityAddEventTimePicker
            )

        }

        binder.activityAddEventCardTime.setOnClickListener {
            changePickersVisibility(
                binder.activityAddEventTimePicker,
                binder.activityAddEventDatePicker
            )

        }


    }

    private fun changePickersVisibility(viewClicked: View, otherView: View) {
        Log.i("fusca", "changePickersVisibility: ops")

        when {
            viewClicked.visibility == View.GONE && otherView.visibility == View.GONE -> {
                viewClicked.visibility = View.VISIBLE
                Log.i("fusca", "changePickersVisibility: 1")
            }
            viewClicked.visibility == View.GONE && otherView.visibility == View.VISIBLE -> {
                otherView.visibility = View.GONE
                viewClicked.visibility = View.VISIBLE
                Log.i("fusca", "changePickersVisibility: 2")

            }
            viewClicked.visibility == View.VISIBLE && otherView.visibility == View.GONE -> {
                viewClicked.visibility = View.GONE
                Log.i("fusca", "changePickersVisibility: 3")
            }
            else -> Log.i("fusca", "changePickersVisibility: caiu no else")


        }

    }

    private fun customizePickers() {

        binder.activityAddEventTimePicker.setIs24HourView(true)
        binder.activityAddEventDatePicker.minDate = calendar.timeInMillis

        bindPickerListeners()


    }

    private fun initDeleteDialog() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(R.string.label_delete_event)
            setMessage(R.string.label_permanent_action)
            setPositiveButton(R.string.label_delete) { _: DialogInterface, _: Int ->
                sendResultBack(eventEditing!!, true)
            }
            setNegativeButton(R.string.label_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()

    }

    private fun retrieveEventIfEditing() {

        eventEditing = intent?.extras?.getParcelable(getString(R.string.extra_key_event))


        eventEditing?.let {
            bindEditingValues()
            isEditing = true
        }

    }

    private fun bindEditingValues() {

        /*
        binder.activityAddEventEditEventTitle.setText(eventEditing?.eventName)
        calendar.timeInMillis = eventEditing?.timestamp!!
        binder.activityAddEventBtnSave.isEnabled = true

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binder.activityAddEventTimePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            binder.activityAddEventTimePicker.minute = calendar.get(Calendar.MINUTE)
        } else {
            binder.activityAddEventTimePicker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            binder.activityAddEventTimePicker.currentMinute = calendar.get(Calendar.MINUTE)
        }

        binder.activityAddEventBtnSave.text = getString(R.string.label_update)

        binder.activityAddEventSwitchEnableNotification.isChecked = eventEditing?.isNotifying!!

        binder.activityAddEditEventTxtDelete.visibility = View.VISIBLE

        updateUiWithCalendarDate()

         */
    }

    private fun bindPickerListeners() {

        binder.activityAddEventDatePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->

            calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            updateUiWithCalendarDate()

        }

        binder.activityAddEventTimePicker.setOnTimeChangedListener { timePicker: TimePicker, i: Int, i1: Int ->

            calendar.set(Calendar.HOUR_OF_DAY, i)
            calendar.set(Calendar.MINUTE, i1)

            updateUiWithCalendarDate()

        }


    }

    private fun updateUiWithCalendarDate() {

        val weekDayDN =
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        val monthDN = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)


        binder.activityAddEventContentDatePicker.contentDatePickerTxtDate.text = getString(R.string.label_dayOfMonth_month_year,dayOfMonth,monthDN,year)


        binder.activityAddEventContentTimePicker.contentTimePickerTxtTime.text = SimpleDateFormat("HH:mm",Locale.getDefault()).format(calendar.timeInMillis)

    }

    private fun createEvent() {

        val eventName =
            binder.activityAddEventEditEventTitle.text.toString()



        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)

        if (isEditing) {
            val event = eventEditing?.copy(
                eventName = eventName,
                timestamp = calendar.timeInMillis,
                isNotifying = binder.activityAddEventSwitchEnableNotification.isChecked,
            )

            sendResultBack(event!!,false)
            return
        }

        val event = Event(
            eventName,
            timestamp = calendar.timeInMillis,
            isNotifying = binder.activityAddEventSwitchEnableNotification.isChecked,
            month =  calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR)
        )

        sendResultBack(event,false)
    }

    private fun sendResultBack(event: Event, shouldDeleteIt: Boolean) {

        val intent = Intent()
        intent.putExtra(getString(R.string.extra_key_event), event)
        intent.putExtra(getString(R.string.extra_key_delete_event), shouldDeleteIt)
        setResult(RESULT_OK, intent)
        finish()

    }
}