package com.simpleplus.timecounter.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.DateFilterAdapter
import com.simpleplus.timecounter.model.DateFilter

class DateFilterHelper(
    private val context: Context, private val recyclerView: RecyclerView,
    private val screenWidth: Int
) {


    fun buildRecyclerView(year: Int) {

        val adapter = DateFilterAdapter(createDateFilters(year))
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        adapter.viewMeasureListener = {
            recyclerView.addItemDecoration(MyItemDecoration(context, screenWidth, it))
        }


        val snapHelper = GravitySnapHelper(Gravity.CENTER) {

            Log.e("Snapped Item Position:", "" + it)
        }


        snapHelper.attachToRecyclerView(recyclerView)
    }


    private fun createDateFilters(year: Int): List<DateFilter> {

        val dateFilterList = mutableListOf<DateFilter>()

        for (month in 0..11) {
            dateFilterList.add(DateFilter(month, year))
        }

        return dateFilterList.toList()
    }

}

