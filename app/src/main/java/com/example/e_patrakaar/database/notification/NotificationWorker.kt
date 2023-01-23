package com.example.e_patrakaar.database.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.e_patrakaar.R
import com.example.e_patrakaar.model.Notification
import com.example.e_patrakaar.utils.Constants
import com.example.e_patrakaar.view.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private lateinit var database: DatabaseReference

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification() {
        val notificationId = 0

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = applicationContext.getString(R.string.notification_title)
        val subtitleNotification = applicationContext.getString(R.string.notification_subtitle)

        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_baseline_logout_24)

        val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .bigLargeIcon(null)

        val pendingIntent : PendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getActivity(applicationContext , 0 , intent , PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, 0)
        }

        val notification =
            NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(titleNotification)
                .setContentText(subtitleNotification)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.notifications_outline)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setStyle(bigPicStyle)
                .setAutoCancel(true)

        notification.priority = NotificationCompat.PRIORITY_HIGH


        notification.setChannelId(Constants.NOTIFICATION_CHANNEL)

        val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

        val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL, Constants.NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH)

        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setSound(ringtoneManager, audioAttributes)
        notificationManager.createNotificationChannel(channel)

        Log.d("error1", "checked")
        notificationManager.notify(notificationId, notification.build())
        Log.d("error1", "failed")

        var currentUser = Firebase.auth.currentUser
        database = Firebase.database.reference
        val listener = FirebaseAuth.AuthStateListener {
            currentUser = it.currentUser
        }

        if(currentUser != null && currentUser!!.isAnonymous){
            val checkOutNotification = Notification(
                R.drawable.cityone,
                "Checkout this new news" ,
                "Suggestion" ,
                "1 min ago"
            )
            database.child("users").child(currentUser!!.uid).child("notifications").push().setValue(checkOutNotification)
        }
    }

    //Convert vector to bitmap
    private fun Context.vectorToBitmap(drawableID: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableID) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}