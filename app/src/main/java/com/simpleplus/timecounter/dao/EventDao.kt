package com.simpleplus.timecounter.dao

import androidx.room.*
import com.simpleplus.timecounter.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert
    suspend fun insert(event:Event):Long

    @Update
    suspend fun update(event:Event)

    @Delete
    suspend fun delete(event:Event)

    @Query ("DELETE FROM event_table")
    suspend fun deleteAll()

    @Query ("SELECT * FROM event_table")
    fun selectAll():Flow<List<Event>>

}