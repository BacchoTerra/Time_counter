package com.simpleplus.timecounter.activities

import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.EventAdapter
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.databinding.ActivityMainBinding
import com.simpleplus.timecounter.databinding.ContentAddEventBinding
import com.simpleplus.timecounter.databinding.ContentSurfaceAddBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.viewmodel.EventViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder: ActivityMainBinding

    //activityResult code
    companion object {
        const val REQ_CODE_ADD_EVENT = 100
    }

    //ViewModel
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(
            (application as EventApplication).eventRepo
        )
    }

    //Recyclerview
    private val adapter: EventAdapter = EventAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        startAddActivity()
        initRecyclerView()
        eventViewModel.deleteAll()
    }


    private fun startAddActivity() {

        binder.activityMainContentFabAdd.contentSurfaceAddFabAdd.setOnClickListener {

            val intent = Intent(this, AddEventActivity::class.java)
            startActivityForResult(intent, REQ_CODE_ADD_EVENT)

        }

    }

    private fun initRecyclerView() {
        val recyclerView = binder.activityMainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQ_CODE_ADD_EVENT) {

            val event: Event = data?.extras?.getParcelable(getString(R.string.extra_key_event))!!

            eventViewModel.insert(event)

        }

    }
}