package com.simpleplus.timecounter.activities

import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.databinding.ActivityMainBinding
import com.simpleplus.timecounter.databinding.ContentAddEventBinding
import com.simpleplus.timecounter.databinding.ContentSurfaceAddBinding
import com.simpleplus.timecounter.model.Event
import com.simpleplus.timecounter.viewmodel.EventViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    //Layout components
    private lateinit var binder:ActivityMainBinding

    //activityResult code
    companion object {const val REQ_CODE_ADD_EVENT = 100}




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        startAddActivity()
    }


    private fun startAddActivity() {

        binder.activityMainContentFabAdd.contentSurfaceAddFabAdd.setOnClickListener {

            val intent = Intent(this,AddEventActivity::class.java)
            startActivityForResult(intent, REQ_CODE_ADD_EVENT)

        }

    }

    private fun initRecyclerView() {



    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQ_CODE_ADD_EVENT) {

            val event:Event = data?.extras?.getParcelable(getString(R.string.extra_key_event)) !!
            val c = Calendar.getInstance()

            c.timeInMillis = event.timestamp
            Log.i("Porsche", "onActivityResult: ${c.time}")

        }

    }
}