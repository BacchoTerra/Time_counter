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
        setContentView(binder.root)


        binder.activityAboutFabSendEmail.setOnClickListener{
            sendEmail()
        }

    }

    /**
     * Opens a BottomSheet to choose an email client to send an email to dev email account
     */
    private fun sendEmail() {

        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.dev_email)))
            type = "message/rfc822"
        }

        try {
            startActivity(Intent.createChooser(intent,"Send eamil"))
        }catch (e:ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

}