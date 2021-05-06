package com.simpleplus.timecounter.repository

import com.simpleplus.timecounter.dao.EventDao
import com.simpleplus.timecounter.database.TimeCounterDatabase
import com.simpleplus.timecounter.model.Event
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao:EventDao) {


    suspend fun insert(event: Event) {

        eventDao.insert(event)

    }

    suspend fun update(event: Event) {

        eventDao.update(event)

    }

    suspend fun delete(event: Event) {

        eventDao.delete(event)

    }


    fun selectAll(): Flow<List<Event>> {

        return eventDao.selectAll()

    }

}