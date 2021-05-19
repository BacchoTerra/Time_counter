package com.simpleplus.timecounter.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.get
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.simpleplus.timecounter.R
import java.util.*

class ChipFilterHelper(
    private val context: Context,
    private val chipGroupMonth: ChipGroup,
    private val chipGroupYear: ChipGroup
) {

    companion object {

        const val NO_SELECTION = -1
        const val MONTH_CORRECTION = 2
        const val INFINITE_YEAR = -2
        const val YEAR_CORRECTION = 14
    }

    //Calendar
    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    val beyondYears = currentYear + 11
    private val yearsArray = intArrayOf(
        NO_SELECTION,
        currentYear,
        currentYear + 1,
        currentYear + 2,
        currentYear + 3,
        currentYear + 4,
        currentYear + 5,
        currentYear + 6,
        currentYear + 7,
        currentYear + 9,
        currentYear + 10,
        INFINITE_YEAR

    )

    //Listeners
    private var monthSelected = NO_SELECTION
    private var yearSelected = NO_SELECTION

    var dateSelectionListener: ((Int, Int) -> Unit)? = null

    //Chips ID
    private var chipsId = 1


    init {

        chipGroupMonth.removeAllViews()
        chipGroupYear.removeAllViews()

        buildMonthGroup()
        buildYearGroup()

        getSelectedDate()
    }


    /**
     * Creates and adds childrens for a monthChipGroup.
     * As the calendar count january as 0, the childrens go from -1 to 11.
     * -1 being no selection, 0 being january, and the rest being the other months
     */
    private fun buildMonthGroup() {

        for (month in -1..11) {

            val chip = LayoutInflater.from(context)
                .inflate(R.layout.content_chip_selection, chipGroupMonth, false) as Chip

            if (month < 0) {
                chip.text = context.getString(R.string.label_none)
                chip.isChecked = true
            } else {
                calendar.set(Calendar.MONTH, month)

                chip.text =
                    calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
            }

            chip.id = chipsId
            chipGroupMonth.addView(chip)
            chipsId +=1
        }

    }

    /**
     * Creates and adds chips to yearsChipGroup
     * it goes for yearsArray containing the necessary years and flags to create the filter chips
     */
    private fun buildYearGroup() {


        for (year in yearsArray) {


            val chip = LayoutInflater.from(context)
                .inflate(R.layout.content_chip_selection, chipGroupYear, false) as Chip

            when (year) {

                NO_SELECTION -> {
                    chip.text = context.getString(R.string.label_none)
                    chip.isChecked = true
                }
                INFINITE_YEAR -> chip.text = "+"
                else -> chip.text = year.toString()

            }
            chip.id = chipsId
            chipGroupYear.addView(chip)
            chipsId +=1
        }

    }

    /**
     * Sends the information through an listener (high level function) to MainActivity.
     */
    private fun getSelectedDate() {

        chipGroupMonth.setOnCheckedChangeListener { _, checkedId ->
            monthSelected = if (checkedId != NO_SELECTION) {
                checkedId - MONTH_CORRECTION
            } else {
                checkedId
            }

            dateSelectionListener?.invoke(monthSelected, yearSelected)


        }

        chipGroupYear.setOnCheckedChangeListener { _, checkedId ->


            yearSelected = when (checkedId) {

                0 -> yearsArray.first()
                (yearsArray.size - 1) - YEAR_CORRECTION -> yearsArray[yearsArray.lastIndex]
                else -> yearsArray[checkedId - YEAR_CORRECTION]

            }


            dateSelectionListener?.invoke(monthSelected, yearSelected)

            if (yearSelected == yearsArray[yearsArray.lastIndex]) disableAllChips() else enableAllChips()

        }

    }

    /**
     * Disable all chips from monthGroup when yearGroup has infiniteYears selected
     */
    private fun disableAllChips() {

        val chip1 = chipGroupMonth[0] as Chip
        chip1.isChecked = true

        for (i in 0 until chipGroupMonth.childCount) {
            val chip = chipGroupMonth[i] as Chip
            chip.isEnabled = false

        }


    }

    /**
     * Enables all chips from monthGroup when yearGroup has deselected infiniteYears
     */
    private fun enableAllChips() {

        val chip1 = chipGroupMonth[0] as Chip
        if (!chip1.isEnabled) {

            for (i in 0 until chipGroupMonth.childCount) {
                val chip = chipGroupMonth[i] as Chip
                chip.isEnabled = true

            }

        }

    }
}