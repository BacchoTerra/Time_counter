package com.simpleplus.timecounter.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.activities.InformationActivity
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class EventAdapter(
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
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    //Listener for switch
    var switchListener: ((Boolean, Event) -> Unit)? = null

    /**
     * Class representing an opened event
     */
    class OpenViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val root: ViewGroup = view.findViewById(R.id.row_open_events_root)
        val txtEventName: TextView = view.findViewById(R.id.row_finished_events_txtEventName)
        val txtDefinedDate: TextView = view.findViewById(R.id.row_finished_events_txtDefinedDate)
        val txtRemainingTime: TextView = view.findViewById(R.id.row_open_events_txtRemainingTime)
        val switchAlarm: SwitchCompat = view.findViewById(R.id.row_open_events_switchAlarm)
        val imageAlarm: ImageView = view.findViewById(R.id.row_open_events_imageAlarm)


    }

    /**
     * Class representing an finished event
     */
    class FinishedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtEventName: TextView = view.findViewById(R.id.row_finished_events_txtEventName)
        val txtDefinedDate: TextView = view.findViewById(R.id.row_finished_events_txtDefinedDate)
        val imageDelete: ImageView = view.findViewById(R.id.row_finished_events_imageDelete)

    }


    /**
     * Comparator for each event on adapter's list
     */
    class EventComparator : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {

            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {

            return oldItem.eventName == newItem.eventName && oldItem.timestamp == newItem.timestamp && oldItem.isNotifying == newItem.isNotifying && oldItem.isFinished == newItem.isFinished && oldItem.month == newItem.month && oldItem.year == newItem.year

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

    /**
     * Method to bind the row's layout when the event is not finished
     *
     * @param event current event on the list
     * @param holder an instance of OpenViewHolder
     */
    private fun bindOpenedViewHolder(event: Event, holder: OpenViewHolder) {

        holder.txtEventName.text = event.eventName
        holder.txtDefinedDate.text = formatDate(event)
        formatAndSetTime(event.timestamp - System.currentTimeMillis(), holder)

        holder.root.setOnClickListener {

            context.startActivity(Intent(context, InformationActivity::class.java).apply {
                putExtra(context.getString(R.string.extra_key_event), event)
            })
        }

        if (event.isNotifying) {
            holder.imageAlarm.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_baseline_access_alarm_24,
                    null
                )
            )

        } else {
            holder.imageAlarm.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_baseline_alarm_off_24,
                    null
                )
            )
        }

        holder.switchAlarm.setOnCheckedChangeListener(null)
        holder.switchAlarm.isChecked = event.isNotifying

        holder.switchAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            switchListener?.invoke(isChecked, event)

            if (isChecked) {
                holder.imageAlarm.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_baseline_access_alarm_24,
                        null
                    )
                )

            } else {
                holder.imageAlarm.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_baseline_alarm_off_24,
                        null
                    )
                )
            }
        }
    }

    /**
     * Method to bind the row's layout when the event finished
     *
     * @param event current event on the list
     * @param holder an instance of FinishedViewHolder
     */
    private fun bindFinishedViewHolder(event: Event, holder: FinishedViewHolder) {

        holder.txtEventName.text = event.eventName
        holder.txtDefinedDate.text = formatDate(event)

        holder.imageDelete.setOnClickListener {
            initDeleteDialog(event)

        }
    }

    /**
     * Creates a string on the format dd/MM/yyyy to bind as defined date to the event to happen
     */
    private fun formatDate(event: Event): String {
        return sdf.format(event.timestamp)

    }

    /**
     * Creates and binds a string with the remaining time to the event defined date
     *
     * @param millisToIt time remaining to event defined date from now
     * @param holder an instance of openViewHolder
     *
     * @see EventAdapter.buildTimeString
     */
    private fun formatAndSetTime(millisToIt: Long, holder: OpenViewHolder) {

        val days = TimeUnit.MILLISECONDS.toDays(millisToIt)
        val hours = TimeUnit.MILLISECONDS.toHours(millisToIt) - TimeUnit.DAYS.toHours(
            TimeUnit.MILLISECONDS.toDays(millisToIt)
        )
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisToIt) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millisToIt)
        )

        holder.txtRemainingTime.text = buildTimeString(days, hours, minutes)
    }

    /**
     * Builds the string with the remaining time to the event from happening
     *
     * @param days how many days missing
     * @param hours how many hours missing
     * @param minutes how many minutes missing
     */
    private fun buildTimeString(days: Long, hours: Long, minutes: Long): String {

        val builder = StringBuilder()

        builder.append(if (days > 9) "${days}d " else "0${days}d ")
        builder.append(if (hours > 9) "${hours}h " else "0${hours}h ")
        builder.append(if (minutes > 9) "${minutes}m " else "0${minutes} m ")

        return builder.toString()

    }

    /**
     * Creates amd shows an alertDialog to confirm event deletion
     *
     * @param event te event to delete
     */
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
