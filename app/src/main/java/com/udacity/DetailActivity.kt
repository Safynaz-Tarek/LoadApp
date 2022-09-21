package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        notificationManager = getSystemService(
            NotificationManager::class.java
        )

        notificationManager.cancelAll()

        status_value.text = intent.getStringExtra("Status")
        file_name_value.text  = intent.getStringExtra("FileName")

    }

    fun navigateToMainScreen(view: View) {
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }


}
