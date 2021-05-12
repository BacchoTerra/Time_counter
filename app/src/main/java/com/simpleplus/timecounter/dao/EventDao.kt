package com.simpleplus.timecounter.dao

import androidx.room.*
import com.simpleplus.timecounter.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.Year

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

    @Query("SELECT * FROM event_table WHERE month = :month")
    fun selectAllFromMonth(month:Int):Flow<List<Event>>

    @Query("SELECT * FROM event_table WHERE year = :year")
    fun selectAllFromYear(year:Int):Flow<List<Event>>

    @Query("SELECT * FROM event_table WHERE month = :month AND year = :year")
    fun selectAllFromMonthAndYear(month:Int,year:Int):Flow<List<Event>>

    @Query("SELECT * FROM event_table WHERE year > :yearPlus ")
    fun selectAllFromBeyond(yearPlus:Int):Flow<List<Event>>



}