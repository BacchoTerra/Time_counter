package com.simpleplus.timecounter.activities

import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.adapter.EventAdapter
import com.simpleplus.timecounter.application.EventApplication
import com.simpleplus.timecounter.databinding.ActivityMainBinding
import com.simpleplus.timecounter.databinding.ContentAddEventBinding
import com.simpleplus.timecounter.databinding.ContentSurfaceAddBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.viewmodel.EventViewModel
import kotlinx.coroutines.GlobalScope
import java.util.*

class MainActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder: ActivityMainBinding

    //ViewModel
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(
            (application as EventApplication).eventRepo
        )
    }

    //Recyclerview
    private val adapter: EventAdapter = EventAdapter(lifecycleScope)

    //ActivityResult
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == RESULT_OK){
            val event: Event = it.data?.extras?.getParcelable(getString(R.string.extra_key_event))!!
            eventViewModel.insert(event)
        }

    }


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
            launcher.launch(intent)

        }

    }

    private fun initRecyclerView() {
        val recyclerView = binder.activityMainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        eventViewModel.allEvents.observe(this, {

            adapter.submitList(it)

            if (it.isNotEmpty()) binder.activityMainTxtNoItensToShow.visibility =
                View.GONE else binder.activityMainTxtNoItensToShow.visibility = View.VISIBLE


        })

    }
}