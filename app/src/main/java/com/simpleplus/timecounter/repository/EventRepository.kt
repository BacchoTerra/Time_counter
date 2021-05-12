package com.simpleplus.timecounter.repository

import com.simpleplus.timecounter.dao.EventDao
import com.simpleplus.timecounter.database.TimeCounterDatabase
import com.simpleplus.timecounter.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.Month

class EventRepository(private val eventDao:EventDao) {


    suspend fun insert(event: Event):Long {

       return eventDao.insert(event)

    }

    suspend fun update(event: Event) {

        eventDao.update(event)

    }

    suspend fun delete(event: Event) {

        eventDao.delete(event)

    }

    suspend fun deleteAll() {

        eventDao.deleteAll()

    }

    fun selectAllEvent ():Flow<List<Event>> {

        return eventDao.selectAll()

    }


    fun selectAllEventFromMonth (month: Int):Flow<List<Event>> {

        return eventDao.selectAllFromMonth(month)

    }


    fun selectAllEventFromYear (year:Int):Flow<List<Event>> {

        return eventDao.selectAllFromYear(year)

    }


    fun selectAllEventFromMonthAndYear (month: Int,year: Int):Flow<List<Event>> {

        return eventDao.selectAllFromMonthAndYear(month,year)

    }

    fun selectAllEventFromBeyond (yearPlus:Int):Flow<List<Event>> {

        return eventDao.selectAllFromBeyond(yearPlus)

    }

}