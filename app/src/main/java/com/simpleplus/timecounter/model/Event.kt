package com.simpleplus.timecounter.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "event_table")
@Parcelize
data class Event(
    val eventName: String,
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    val timestamp: Long,
    val isFinished:Boolean = false,
    val isNotifying:Boolean = true,
    val month:Int,
    val year:Int

) : Parcelable
