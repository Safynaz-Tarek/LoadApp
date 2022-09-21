package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val CHANNEL_ID = "channelId"
class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var URL_CHOICE : DownloadType
    private val NOTIFICATION_ID = 1
    private var current_status: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            CHANNEL_ID,
            "LoadAppChannel"
        )

        custom_button.setOnClickListener {
            download()
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L)
            val action = intent?.action


            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    current_status = "Success"
                    custom_button.buttonState = ButtonState.Completed

                }else{
                    custom_button.buttonState = ButtonState.Completed
                    current_status = "Failed"
                }
            }
            when(URL_CHOICE){
                DownloadType.UDACITY -> showNotification(getString(R.string.udacityString),current_status)
                DownloadType.GLIDE -> showNotification(getString(R.string.glideString),current_status)
                DownloadType.RETROFIT -> showNotification(getString(R.string.retrofitString),current_status)
            }
        }
    }

    fun radio_choice(view: View)
    {
        when(view.id)
        {
            glide_choice.id-> URL_CHOICE = DownloadType.GLIDE
            udacity_choice.id-> URL_CHOICE = DownloadType.UDACITY
            retrofit_choice.id-> URL_CHOICE = DownloadType.RETROFIT

        }
    }

    private fun download() {

        if(!::URL_CHOICE.isInitialized){
            Toast.makeText(applicationContext, "Please select one of the above options", Toast.LENGTH_LONG).show()
        }else{

            val request =
                DownloadManager.Request(Uri.parse(URL_CHOICE.url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            custom_button.buttonState = ButtonState.Loading
        }
    }

    fun showNotification(file_name:String , status:String) {

        val bundle = Bundle()
        bundle.putString("FileName",file_name)
        bundle.putString("Status",status)

        val detailsIntent = Intent(this,DetailActivity::class.java).apply {
            putExtras(bundle)
        }
        pendingIntent = PendingIntent.getActivity(this,0,detailsIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        action = NotificationCompat.Action(0,"Check status" ,pendingIntent)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .apply {
                addAction(action)
                setSubText(status)
                setAutoCancel(true)
                priority = NotificationCompat.PRIORITY_HIGH
                setContentTitle("Check Your Request")
                setContentText("Request Finished")
                setSmallIcon(R.drawable.ic_launcher_foreground)
            }.build()

        notificationManager.notify(NOTIFICATION_ID,notification)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "LoadApp Download Request"

            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }

    }

    enum class DownloadType(val url: String) {
        UDACITY(
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        ),
        GLIDE(
            "https://github.com/bumptech/glide/archive/master.zip"
        ),
        RETROFIT(
            "https://github.com/square/retrofit/archive/master.zip"
        );
    }


}
