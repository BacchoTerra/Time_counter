package com.simpleplus.timecounter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.model.DateFilter
import java.util.*

class DateFilterAdapter(private val list: List<DateFilter>) :
    RecyclerView.Adapter<DateFilterAdapter.MyViewHolder>() {

    companion object {
        const val CONST_ITEMS_COUNT = 12
    }

    private val c = Calendar.getInstance()

    //Listener to measure view width
    var viewMeasureListener: ((Int) -> Unit)? = null


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtMonth: TextView = view.findViewById(R.id.row_date_filter_txtMonth)
        val txtYear: TextView = view.findViewById(R.id.row_date_filter_txtYear)
        val root: ViewGroup = view.findViewById(R.id.row_date_filter_root)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemList =
            LayoutInflater.from(parent.context).inflate(R.layout.row_date_filter, parent, false)

        return MyViewHolder(itemList)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dateFilter = list[position]

        if (position == 0) {
            measureRowWidth(holder)
        }

        holder.txtMonth.text = getMonthDisplayName(dateFilter.month)
        holder.txtYear.text = dateFilter.year.toString()

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getMonthDisplayName(month:Int):String {
        c.set(Calendar.MONTH,month)
        return c.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault())!!
    }

    private fun measureRowWidth(holder:MyViewHolder) {

        val view = holder.root

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                viewMeasureListener?.invoke(view.width)
                Log.i("Porsche", "onGlobalLayout: ${view.width}")

            }
        })

    }
}