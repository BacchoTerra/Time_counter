package com.simpleplus.timecounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.simpleplus.timecounter.dao.EventDao
import com.simpleplus.timecounter.model.Event

@Database(entities = [Event::class],version = 1)
abstract class TimeCounterDatabase:RoomDatabase (){

    abstract fun EventDao():EventDao

    companion object {

        @Volatile
        private var INSTANCE:TimeCounterDatabase? = null

        fun getInstance(context:Context):TimeCounterDatabase {

            return INSTANCE?: synchronized(this) {

                val instance = Room.databaseBuilder(context,TimeCounterDatabase::class.java,"timecounter_database").fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }


        }

    }

}