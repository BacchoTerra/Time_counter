package com.simpleplus.timecounter.utils

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.simpleplus.timecounter.R
import java.util.*

class ChipFilterHelper(
    private val context: Context,
    private val chipGroupMonth: ChipGroup,
    private val chipGroupYear: ChipGroup
) {

    private val calendar = Calendar.getInstance()

    init {

        buildMonthGroup()
        buildYearGroup()
    }

    private fun buildMonthGroup() {

        for (month in 0..11) {
            calendar.set(Calendar.MONTH, month)

            val chip = LayoutInflater.from(context)
                .inflate(R.layout.content_chip_selection, chipGroupMonth,false) as Chip
            chip.text = calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault())

            chipGroupMonth.addView(chip)
        }

    }

    private fun buildYearGroup() {

        val currentYear = calendar.get(Calendar.YEAR)

        for (year in currentYear..currentYear + 11) {

            val chip = LayoutInflater.from(context)
                .inflate(R.layout.content_chip_selection, chipGroupYear,false
                ) as Chip

            if (year == currentYear + 11) {
                chip.text = context.getString(R.string.label_current_year_plus, year)
                chipGroupYear.addView(chip)
                continue
            }

            chipGroupYear.addView(chip.apply { text = year.toString() })

        }

    }

}