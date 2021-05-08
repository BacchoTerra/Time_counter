package com.simpleplus.timecounter.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "event_table")
@Parcelize
data class Event(
    val eventName: String,
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    val timestamp: Long
) : Parcelable
