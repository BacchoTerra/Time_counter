package com.simpleplus.timecounter.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TimePicker
import android.widget.Toast
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
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityAddEditEventBinding.inflate(layoutInflater)
        setContentView(binder.root)

        retrieveEventIfEditing()
        handlePickersVisibility()
        customizePickers()
        updateUiWithCalendarDate()
        hideKeyboard()


        binder.activityAddEventTxtSave.setOnClickListener {

            hideKeyboard()
            if (binder.activityAddEventEditEventTitle.text.isEmpty()) {
                binder.activityAddEventEditEventTitle.error = "*"
            } else {
                createEvent()
            }

        }

        binder.activityAddEventTxtCancel.setOnClickListener {

            hideKeyboard()
            finish()

        }


    }


    /**
     * Handle the date and time cads click events
     * @see AddEventActivity.changePickersVisibility
     */
    private fun handlePickersVisibility() {
        binder.activityAddEventCardDate.setOnClickListener {

            hideKeyboard()

            changePickersVisibility(
                binder.activityAddEventDatePicker,
                binder.activityAddEventTimePicker
            )

        }

        binder.activityAddEventCardTime.setOnClickListener {

            hideKeyboard()
            changePickersVisibility(
                binder.activityAddEventTimePicker,
                binder.activityAddEventDatePicker
            )

        }


    }

    /**
     * Changes the visibility of each time and date picker
     */
    private fun changePickersVisibility(viewClicked: View, otherView: View) {

        when {
            viewClicked.visibility == View.GONE && otherView.visibility == View.GONE -> {
                viewClicked.visibility = View.VISIBLE
            }
            viewClicked.visibility == View.GONE && otherView.visibility == View.VISIBLE -> {
                otherView.visibility = View.GONE
                viewClicked.visibility = View.VISIBLE

            }
            viewClicked.visibility == View.VISIBLE && otherView.visibility == View.GONE -> {
                viewClicked.visibility = View.GONE

            }

        }

    }

    /**
     * configures initial properties of time and date pickers
     */
    private fun customizePickers() {

        binder.activityAddEventTimePicker.setIs24HourView(true)
        binder.activityAddEventDatePicker.minDate = System.currentTimeMillis() - 10000

        bindPickerListeners()


    }

    /**
     * If adding or editing an event, this activty is used. this method checks if there is an extra in the activities intent
     * @see AddEventActivity.bindEditingValues
     */
    private fun retrieveEventIfEditing() {

        eventEditing = intent?.extras?.getParcelable(getString(R.string.extra_key_event))


        eventEditing?.let {
            bindEditingValues()
            isEditing = true
        }

    }

    /**
     * If there is an extra on  this activity intent, this method will be triggered and will set all the layout fields
     * with the editing event data
     * @see AddEventActivity.retrieveEventIfEditing
     */
    private fun bindEditingValues() {


        binder.activityAddEventEditEventTitle.setText(eventEditing?.eventName)
        calendar.timeInMillis = eventEditing?.timestamp!!

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binder.activityAddEventTimePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            binder.activityAddEventTimePicker.minute = calendar.get(Calendar.MINUTE)
        } else {
            binder.activityAddEventTimePicker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            binder.activityAddEventTimePicker.currentMinute = calendar.get(Calendar.MINUTE)
        }

        binder.activityAddEventTxtSave.text = getString(R.string.label_update)

        binder.activityAddEventSwitchEnableNotification.isChecked = eventEditing?.isNotifying!!

        updateUiWithCalendarDate()


    }

    /**
     * Changes the activity calendar instance date and time accordingly to the spinner of the pickers
     */
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

        binder.activityAddEventTimePicker.setOnTimeChangedListener { _: TimePicker, i: Int, i1: Int ->

            calendar.set(Calendar.HOUR_OF_DAY, i)
            calendar.set(Calendar.MINUTE, i1)

            updateUiWithCalendarDate()

        }


    }

    /**
     * Updates textViews texts to the selected date on the pickers
     * or if editing, to the event time
     */
    private fun updateUiWithCalendarDate() {


        val monthDN = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)


        binder.activityAddEventContentDatePicker.contentDatePickerTxtDate.text =
            getString(R.string.label_dayOfMonth_month_year, dayOfMonth, monthDN, year)


        binder.activityAddEventContentTimePicker.contentTimePickerTxtTime.text =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.timeInMillis)

    }

    /**
     * Constructs an Event object with all the information gathered from input fields.
     * It handles if the event is being edited or if its a new addition
     */
    private fun createEvent() {

        if ( System.currentTimeMillis() <= calendar.timeInMillis) {


            val eventName =
                binder.activityAddEventEditEventTitle.text.toString()



            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            if (isEditing) {
                val event = eventEditing?.copy(
                    eventName = eventName,
                    timestamp = calendar.timeInMillis,
                    month = calendar.get(Calendar.MONTH),
                    year = calendar.get(Calendar.YEAR),
                    isNotifying = binder.activityAddEventSwitchEnableNotification.isChecked,
                )

                sendResultBack(event!!)
                return
            }

            val event = Event(
                eventName,
                timestamp = calendar.timeInMillis,
                isNotifying = binder.activityAddEventSwitchEnableNotification.isChecked,
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR)
            )

            sendResultBack(event)
        }else{
            Toast.makeText(this,R.string.toast_min_one_minute,Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Send the created or updated event back to the activity that called this acctivity
     */
    private fun sendResultBack(event: Event) {

        val intent = Intent()
        intent.putExtra(getString(R.string.extra_key_event), event)
        setResult(RESULT_OK, intent)
        finish()

    }

    /**
     * Hides the softKeyBoardInput if other view other then the editText gets focus
     */
    private fun hideKeyboard() {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (imm.isActive) {
            imm.hideSoftInputFromWindow(binder.activityAddEventEditEventTitle.windowToken,0)
        }

    }
}