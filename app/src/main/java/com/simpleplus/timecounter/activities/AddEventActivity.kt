package com.simpleplus.timecounter.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.TimePicker
import android.widget.Toast
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.databinding.ContentAddEventBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.utils.DatePickerUtil
import com.simpleplus.timecounter.utils.DatePickerUtil.dateListener
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder: ContentAddEventBinding

    //Timestamp from materialDatePicker
    private val calendar = Calendar.getInstance()
    private var isDateChosen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ContentAddEventBinding.inflate(layoutInflater)
        setContentView(binder.root)

        initToolbar()
        handleTimePickerVisibility()
        showDatePicker()

        enableSaveButton()

        binder.contentAddEventBtnSave.setOnClickListener {
            createEvent()
        }
    }

    private fun initToolbar() {
        val toolbar = binder.contentAddEventToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

    }

    private fun handleTimePickerVisibility() {

        val timePicker = binder.contentAddEventTimePicker


        binder.contentAddEventSwitchAddTime.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->

            timePicker.visibility = if (b) View.VISIBLE else View.GONE
        }

    }

    private fun showDatePicker() {

        binder.contentAddEventTxtDefineDate.setOnClickListener {
            DatePickerUtil.showPicker(supportFragmentManager)
        }

    }

    private fun getAndHandleDateTimestamp(){

        dateListener = { returnedTimestamp ->

            calendar.timeInMillis = returnedTimestamp
            updateUiWithTimestamp()
            isDateChosen = true

            binder.contentAddEventBtnSave.isEnabled =
                binder.contentAddEventEditEventTitle.text.toString().isNotEmpty()

        }

    }

    private fun updateUiWithTimestamp() {

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binder.contentAddEventTxtDefineDate.text = sdf.format(calendar.timeInMillis)

    }

    private fun enableSaveButton() {

        binder.contentAddEventEditEventTitle.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binder.contentAddEventBtnSave.isEnabled = count >0 && isDateChosen
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        getAndHandleDateTimestamp()


    }

    private fun createEvent() {

        val eventName = binder.contentAddEventEditEventTitle.text.toString()

        defineTimestampHours()
        val event = Event(eventName, timestamp = calendar.timeInMillis)
        sendResultBack(event)


    }

    private fun defineTimestampHours() {

        if (binder.contentAddEventSwitchAddTime.isChecked) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                calendar.set(Calendar.HOUR_OF_DAY, binder.contentAddEventTimePicker.hour)
                calendar.set(Calendar.MINUTE, binder.contentAddEventTimePicker.minute)
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, binder.contentAddEventTimePicker.currentHour)
                calendar.set(Calendar.MINUTE, binder.contentAddEventTimePicker.currentMinute)
            }


        }else{
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
        }
    }

    private fun sendResultBack(event: Event) {

        val intent = Intent()
        intent.putExtra(getString(R.string.extra_key_event),event)
        setResult(RESULT_OK, intent)
        finish()

    }

}