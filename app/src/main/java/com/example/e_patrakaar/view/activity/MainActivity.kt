package com.example.e_patrakaar.view.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.e_patrakaar.R
import com.example.e_patrakaar.database.notification.NotificationWorker
import com.example.e_patrakaar.databinding.ActivityMainBinding
import com.example.e_patrakaar.model.Notification
import com.example.e_patrakaar.utils.Constants
import com.example.e_patrakaar.view.fragment.main.NotificationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIFIACTION_ID = 0
    val list : MutableLiveData<List<Notification>> = MutableLiveData()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Time" , "create notification called")
        createNotificationChannel()

//        val notificationManager = NotificationManagerCompat.from(this)
//        binding.navView.setOnClickListener {
//            notificationManager.notify(NOTIFIACTION_ID,notification)
//        }

        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_leaderboard,
                R.id.navigation_search,
                R.id.navigation_notification,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            binding.navView.selectedItemId = R.id.navigation_notification
        }

        startWork()

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        // when app is installed for first time it gives initial time 5:30 am
        // so first time when app is installed it will not show notification as they are blocked in settings but the time
        // in shared pref is updated with the current time

        val storedTime = Instant.ofEpochMilli(
            sharedPref.getLong(
                getString(R.string.greeting_notif_shared_pref_key),
                0
            )
        ).atZone(ZoneId.systemDefault()).toLocalTime()

        // night from 9:00pm to 5:59 am i.e ( 21:00 - 5:59)
        // morning from 6:00am to 11:59 am i.e (6:00 to 11:59)
        // afternoon from 12:00pm to 3:59pm i.e (12:00 to 15:59)
        // evening from 4:00pm to 8:59 i.e (16:00 to 20:59)

        val currentTime = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalTime()

        val storedTimeHour = storedTime.hour
        val currentHour = currentTime.hour
        var showNotification = true
        var greetings = "Hello user"

        // morning
        if((currentHour>6 || currentHour == 6) && currentHour < 12){
            greetings = getString(R.string.notification_greeting_morning)
            if((storedTimeHour>6 || storedTimeHour == 6) && storedTimeHour < 12){
                showNotification = false
            }
        }
        // afternoon
        else if((currentHour>12 || currentHour == 12) && currentHour < 16){
            greetings = getString(R.string.notification_greeting_afternooon)
            if((storedTimeHour>12 || storedTimeHour == 12) && storedTimeHour < 16){
                showNotification = false
            }
        }
        // evening
        else if((currentHour>16 || currentHour == 16) && currentHour < 21){
            greetings = getString(R.string.notification_greeting_evening)
            if((storedTimeHour>16 || storedTimeHour == 16) && storedTimeHour < 21){
                showNotification = false
            }
        }
        // night check
        else if(
            ((currentHour>21 || currentHour == 21) && currentHour < 24) ||
            ((currentHour > 0 || currentHour == 0)&& currentHour < 6)
        ){
            greetings = getString(R.string.notification_greeting_night)
            if(
                ((storedTimeHour>21 || storedTimeHour == 21) && storedTimeHour < 24) ||
                ((storedTimeHour > 0 || storedTimeHour == 0)&& storedTimeHour < 6)
            ){
                showNotification = false
            }
        }

        if (showNotification) {
            createAndShowNotification(greetings)

            with(sharedPref.edit()){
                putLong(getString(R.string.greeting_notif_shared_pref_key) , System.currentTimeMillis()).apply()
            }

            val notificationList = NotificationFragment.dummyList()
            notificationList.add(
                Notification(R.drawable.cityone , greetings , "Greetings" , "1 min ago")
            )
            list.postValue(notificationList)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lightColor = Color.GREEN
            enableLights(true)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)
        .build()

    private fun createWorkRequest() =
        PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.SECONDS).setConstraints(
            createConstraints()
        ).build()

    private fun startWork() {
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "News Notify Work",
                ExistingPeriodicWorkPolicy.KEEP,
                createWorkRequest()
            )
    }

    private fun createAndShowNotification(content: String) {

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Greetings....")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setSmallIcon(R.drawable.notification_selector)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFIACTION_ID, notification)
    }
}