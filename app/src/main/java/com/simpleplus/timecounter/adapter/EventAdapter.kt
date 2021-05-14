package com.simpleplus.timecounter.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.activities.AddEventActivity
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class EventAdapter(
    private val launcher: ActivityResultLauncher<Intent>,
    private val context: Context,
    private val viewModel: EventViewModel
) :
    ListAdapter<Event, RecyclerView.ViewHolder>(EventComparator()) {

    //ViewTypes
    companion object {
        const val TYPE_OPEN = 0
        const val TYPE_FINISHED = 1
    }

    //Date formatter
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    class OpenViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val root: ViewGroup = view.findViewById(R.id.row_open_events_root)
        val txtEventName: TextView = view.findViewById(R.id.row_open_events_txtEventName)
        val txtDefinedDate: TextView = view.findViewById(R.id.row_open_events_txtDefinedDate)
        val txtRemainingTime: TextView = view.findViewById(R.id.row_open_events_txtRemainingTime)
        val switchAlarm : SwitchCompat = view.findViewById(R.id.row_open_events_switchAlarm)


    }

    class FinishedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtEventName: TextView = view.findViewById(R.id.row_finished_events_txtEventName)
        val txtDefinedDate: TextView = view.findViewById(R.id.row_finished_events_txtDefinedDate)
        val txtRemainingTime: TextView =
            view.findViewById(R.id.row_finished_events_txtRemainingTime)
        val txtDelete: TextView =
            view.findViewById(R.id.row_finished_events_txtDelete)


    }


    class EventComparator : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {

            return oldItem === newItem

        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {

            return oldItem.eventName == newItem.eventName && oldItem.timestamp == newItem.timestamp

        }

    }

    override fun getItemViewType(position: Int): Int {

        val event = getItem(position)

        return if (event.isFinished) return TYPE_FINISHED else TYPE_OPEN

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            TYPE_OPEN -> OpenViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_open_events, parent, false)
            )

            else -> FinishedViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_finished_events, parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val event = getItem(position)

        when (holder) {

            is OpenViewHolder -> bindOpenedViewHolder(event, holder)

            else -> bindFinishedViewHolder(event, holder as FinishedViewHolder)

        }


    }

    private fun bindOpenedViewHolder(event: Event, holder: OpenViewHolder) {

        holder.txtEventName.text = event.eventName
        holder.txtDefinedDate.text = formatDate(event)
        formatAndSetTime(event.timestamp - System.currentTimeMillis(), holder)

        holder.root.setOnClickListener {

            launcher.launch(Intent(Intent(context, AddEventActivity::class.java).apply {
                putExtra(
                    context.getString(R.string.extra_key_event),
                    event
                )
            }))
        }

        holder.switchAlarm.isChecked = event.isNotifying

    }

    private fun bindFinishedViewHolder(event: Event, holder: FinishedViewHolder) {

        holder.txtEventName.text = event.eventName
        holder.txtDefinedDate.text = formatDate(event)

        holder.txtDelete.setOnClickListener {
            initDeleteDialog(event)

        }
    }

    private fun formatDate(event: Event): String {
        return sdf.format(event.timestamp)

    }

    private fun formatAndSetTime(millisToIt: Long, holder: OpenViewHolder) {

        val days = TimeUnit.MILLISECONDS.toDays(millisToIt)
        val hours = TimeUnit.MILLISECONDS.toHours(millisToIt) - TimeUnit.DAYS.toHours(
            TimeUnit.MILLISECONDS.toDays(millisToIt)
        )
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisToIt) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millisToIt)
        )
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisToIt) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millisToIt)
        )

        holder.txtRemainingTime.text = buildTimeString(days, hours, minutes, seconds)
    }

    private fun buildTimeString(days: Long, hours: Long, minutes: Long, seconds: Long): String {

        val builder = StringBuilder()

        builder.append(if (days > 9) "${days}d " else "0${days}d ")
        builder.append(if (hours > 9) "${hours}h " else "0${hours}h ")
        builder.append(if (minutes > 9) "${minutes}m " else "0${minutes} m ")
        //builder.append(if (seconds > 9) "$seconds " else "0$seconds")

        return builder.toString()

    }

    private fun initDeleteDialog(event: Event) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(R.string.label_delete_event)
            setMessage(R.string.label_permanent_action)
            setPositiveButton(R.string.label_delete) { _: DialogInterface, _: Int ->

                viewModel.delete(event)

            }
            setNegativeButton(R.string.label_cancel) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()

    }
}
