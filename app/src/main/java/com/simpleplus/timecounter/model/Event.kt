package com.simpleplus.timecounter.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")

data class Event(
    val eventName: String,
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timestamp: Long
)
