package com.simpleplus.timecounter.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(private val repo: EventRepository) : ViewModel() {


    var lastId = 0L

    fun insert(event: Event) = viewModelScope.launch {
        lastId = repo.insert(event)

    }

    fun update(event: Event) = viewModelScope.launch {
        repo.update(event)
    }

    fun delete(event: Event) = viewModelScope.launch {
        repo.delete(event)
    }

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
    }

    fun selectAll(): LiveData<List<Event>> {

        return repo.selectAllEvent().asLiveData()

    }

    fun selectAllFromMonth(month: Int): LiveData<List<Event>> {

        return repo.selectAllEventFromMonth(month).asLiveData()
    }

    fun selectAllFromYear(year: Int): LiveData<List<Event>> {

        return repo.selectAllEventFromYear(year).asLiveData()

    }

    fun selectAllFromMonthAndYear(month:Int,year:Int) :LiveData<List<Event>> {

        return repo.selectAllEventFromMonthAndYear(month,year).asLiveData()

    }

    fun selectAllFromBeyond(yearPlus:Int) :LiveData<List<Event>> {

        return repo.selectAllEventFromBeyond(yearPlus).asLiveData()

    }


    class EventViewModelFactory(private val repo: EventRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventViewModel(repo) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")

        }

    }


}