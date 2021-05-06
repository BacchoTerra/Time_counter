package com.simpleplus.timecounter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(private val repo: EventRepository) : ViewModel() {

    fun insert(event: Event) = viewModelScope.launch {
        repo.insert(event)
    }

    fun update(event: Event) = viewModelScope.launch {
        repo.update(event)
    }

    fun delete(event: Event) = viewModelScope.launch {
        repo.delete(event)
    }

    fun selectAll(): LiveData<List<Event>> {

        return repo.selectAll().asLiveData()

    }


}