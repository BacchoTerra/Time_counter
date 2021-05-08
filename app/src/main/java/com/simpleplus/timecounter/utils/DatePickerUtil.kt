package com.simpleplus.timecounter.utils

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.util.*

object DatePickerUtil : MaterialPickerOnPositiveButtonClickListener<Long> {

    //MaterialDate
    private var materialDatePicker: MaterialDatePicker<Long> = MaterialDatePicker()
    private var builder = MaterialDatePicker.Builder.datePicker()

    //Listener
    var dateListener: ((timestamp:Long) -> Unit)? = null

    //Value
    private const val MILLIS_DAY:Long = 86400000

    fun showPicker(manager: FragmentManager) {

        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        builder.setCalendarConstraints(limitRange().build())
        materialDatePicker = builder.build()
        materialDatePicker.addOnPositiveButtonClickListener(this)
        materialDatePicker.show(manager, null)

    }

    override fun onPositiveButtonClick(selection: Long?) {

        dateListener?.invoke(selection!! + MILLIS_DAY)

    }

    private fun limitRange(): CalendarConstraints.Builder {

        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarStart: Calendar = GregorianCalendar.getInstance()

        val year = calendarStart.get(Calendar.YEAR)

        calendarStart.set(
            year,
            calendarStart.get(Calendar.MONTH),
            calendarStart.get(Calendar.DAY_OF_MONTH) - 1
        )

        val minDate = calendarStart.timeInMillis

        constraintsBuilderRange.setStart(minDate)

        constraintsBuilderRange.setValidator(RangeValidator(minDate))

        return constraintsBuilderRange
    }

    private class RangeValidator(private val minDate: Long) :
        CalendarConstraints.DateValidator {


        constructor(parcel: Parcel) : this(
            parcel.readLong()
        )

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            TODO("not implemented")
        }

        override fun describeContents(): Int {
            TODO("not implemented")
        }

        override fun isValid(date: Long): Boolean {
            return minDate <= date

        }

        companion object CREATOR : Parcelable.Creator<RangeValidator> {
            override fun createFromParcel(parcel: Parcel): RangeValidator {
                return RangeValidator(parcel)
            }

            override fun newArray(size: Int): Array<RangeValidator?> {
                return arrayOfNulls(size)
            }
        }

    }
}

