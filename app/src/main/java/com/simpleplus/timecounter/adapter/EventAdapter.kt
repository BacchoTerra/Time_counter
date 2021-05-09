package com.simpleplus.timecounter.adapter

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.model.Event
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class EventAdapter(private val lifeCycle: LifecycleCoroutineScope) :
    ListAdapter<Event, EventAdapter.MyViewHolder>(EventComparator()) {

    private val calendar = Calendar.getInstance()
    private var job: Job? = null

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

            return oldItem.eventName == newItem.eventName && oldItem.timestamp == newItem.timestamp

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemList =
            LayoutInflater.from(parent.context).inflate(R.layout.row_events, parent, false)

        return MyViewHolder(itemList)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val event = getItem(position)

        bindViews(event, holder)

    }

    private fun bindViews(event: Event, holder: MyViewHolder) {

        holder.txtEventName.text = event.eventName
        holder.txtDefinedDate.text = formatDate(event)

        formatTime(event.timestamp,holder)

    }

    private fun formatTime(eventTimestamp: Long,holder: MyViewHolder) {

        job = lifeCycle.launch(Dispatchers.IO) {

            val timeRemaining = eventTimestamp - calendar.timeInMillis

            val totalSeconds = timeRemaining / 1000
            var seconds = 0
            var minutes = 0
            var hours = 0
            var days = 0

            for (i in 1..totalSeconds) {

                seconds++

                if (seconds == 60) {

                    seconds = 0
                    minutes++

                    if (minutes == 60) {
                        minutes = 0
                        hours++

                        if (hours == 24) {
                            days++
                            hours = 0
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                buildTimeString(days, hours, minutes, seconds,holder)
            }
        }

        if (job?.isCompleted!!) job?.cancel()
    }

    private fun buildTimeString(days: Int, hours: Int, minutes: Int, seconds: Int,holder: MyViewHolder){

        val builder = StringBuilder()

        builder.append(if (days > 9) "$days: " else "0$days: ")
        builder.append(if (hours > 9) "$hours: " else "0$hours: ")
        builder.append(if (minutes > 9) "$minutes: " else "0$minutes: ")
        builder.append(if (seconds > 9) "$seconds: " else "0$seconds")


        holder.txtRemainingTime.text =  builder.toString()

    }

    private fun formatDate(event: Event): String {

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(event.timestamp)

    }

    private fun startTimer(timestamp: Long, holder: MyViewHolder) {

        val timer = object : CountDownTimer(timestamp, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                TODO("Not yet implemented")
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }

        }

    }
}