package com.simpleplus.timecounter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.model.Event
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter : ListAdapter<Event, EventAdapter.MyViewHolder>(EventComparator()) {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtEventName: TextView = view.findViewById(R.id.row_events_txtEventName)
        val txtDefinedDate: TextView = view.findViewById(R.id.row_events_txtDefinedDate)
        val txtRemainingTime: TextView = view.findViewById(R.id.row_events_txtRemainingTime)

    }

    class EventComparator : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {

            return oldItem === newItem

        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {

            return oldItem.eventName == newItem.eventName && oldItem.id == newItem.id && oldItem.timestamp == newItem.timestamp

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemList =
            LayoutInflater.from(parent.context).inflate(R.layout.row_events, parent, false)

        return MyViewHolder(itemList)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val event = getItem(position)

        bindViews(event,holder)

    }

    private fun bindViews(event:Event,holder:MyViewHolder) {

        holder.txtEventName.text = event.eventName
        holder.txtDefinedDate.text = formatDate(event)

    }

    private fun formatDate(event: Event):String {

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(event.timestamp)

    }

}