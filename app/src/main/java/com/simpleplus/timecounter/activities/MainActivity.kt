package com.simpleplus.timecounter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.EventAdapter
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.databinding.ActivityMainBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.viewmodel.EventViewModel

class MainActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder: ActivityMainBinding

    //ViewModel
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(
            (application as EventApplication).eventRepo
        )
    }

    //ActivityResult
    private val addEventLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val event: Event =
                    it.data?.extras?.getParcelable(getString(R.string.extra_key_event))!!
                eventViewModel.insert(event)
            }

        }

    private val editEventLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == RESULT_OK) {

                val event: Event =
                    it.data?.extras?.getParcelable(getString(R.string.extra_key_event))!!

                if (it.data?.extras?.getBoolean(getString(R.string.extra_key_delete_event))!!) {
                    eventViewModel.delete(event)
                } else {
                    eventViewModel.update(event)
                }

            }

        }

    //Recyclerview
    private val adapter: EventAdapter = EventAdapter(editEventLauncher, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        startAddActivity()
        initRecyclerView()
    }


    private fun startAddActivity() {

        binder.activityMainContentFabAdd.contentSurfaceAddFabAdd.setOnClickListener {

            val intent = Intent(this, AddEventActivity::class.java)
            addEventLauncher.launch(intent)

        }

    }

    private fun initRecyclerView() {
        val recyclerView = binder.activityMainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        eventViewModel.allEvents.observe(this, {


            autoUpdateEventsAndAdapter(it, adapter)

            if (it.isNotEmpty()) binder.activityMainTxtNoItensToShow.visibility =
                View.GONE else binder.activityMainTxtNoItensToShow.visibility = View.VISIBLE


        })

    }

    private fun autoUpdateEventsAndAdapter(list: List<Event>, adapter: EventAdapter) {

        val currentTime = System.currentTimeMillis()

        for (event in list) {
            if (!event.isFinished && event.timestamp <= currentTime) {
                eventViewModel.update(event.copy(isFinished = true))
            }
        }

        adapter.submitList(list)

    }
}