package com.simpleplus.timecounter.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "event_table")
@kotlinx.parcelize.Parcelize
data class Event(
    val eventName: String,
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    val timestamp: Long,
    val isFinished:Boolean = false,
    val isNotifying:Boolean = true,

) : Parcelable
