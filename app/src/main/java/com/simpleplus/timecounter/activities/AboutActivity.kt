package com.simpleplus.timecounter.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.simpleplus.timecounter.R
import com.simpleplus.timecounter.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    //Layout component
    private lateinit var binder:ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_about)


        binder.activityAboutFabSendEmail.setOnClickListener{
            sendEmail()
        }

    }

    private fun sendEmail() {

        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL,getString(R.string.dev_email))
            type = "message/rfc822"
        }

        try {
            startActivity(Intent.createChooser(intent,"Send eamil"))
        }catch (e:ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

}