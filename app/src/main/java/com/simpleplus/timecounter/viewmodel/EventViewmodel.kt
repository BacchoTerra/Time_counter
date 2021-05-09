package com.simpleplus.timecounter.viewmodel

import androidx.lifecycle.*
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(private val repo: EventRepository) : ViewModel() {

    val allEvents:LiveData<List<Event>> = repo.allEvents.asLiveData()

    fun insert(event: Event) = viewModelScope.launch {
        repo.insert(event)
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

    class EventViewModelFactory(private val repo:EventRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventViewModel(repo) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")

        }

    }


}