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

    @Query ("SELECT * FROM event_table ORDER BY timestamp")
    fun selectAll():Flow<List<Event>>

    @Query("SELECT * FROM event_table WHERE month = :month ORDER BY timestamp")
    fun selectAllFromMonth(month:Int):Flow<List<Event>>

    @Query("SELECT * FROM event_table WHERE year = :year ORDER BY timestamp")
    fun selectAllFromYear(year:Int):Flow<List<Event>>

    @Query("SELECT * FROM event_table WHERE month = :month AND year = :year ORDER BY timestamp")
    fun selectAllFromMonthAndYear(month:Int,year:Int):Flow<List<Event>>

    @Query("SELECT * FROM event_table WHERE year > :yearPlus ORDER BY timestamp")
    fun selectAllFromBeyond(yearPlus:Int):Flow<List<Event>>



}