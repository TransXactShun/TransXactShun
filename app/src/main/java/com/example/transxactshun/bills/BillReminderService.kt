package com.example.transxactshun.bills

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.transxactshun.MainActivity
import com.example.transxactshun.R

class BillReminderService : Service() {

    private lateinit var myBinder: MyBinder
    private var msgHandler: Handler? = null

    // vars for Notification Manager
    private lateinit var notificationManager: NotificationManager
    private val notificationId = 989
    private val channelId = "notification_channel"

    override fun onCreate() {
        super.onCreate()

        myBinder = MyBinder()

        // display notification
        displayNotification()

        // stop the service after displaying notification
        stopSelf()

    }

    // Reference taken from Lecture 13: BindDemoKotlin
    // Link: https://www.sfu.ca/~xingdong/Teaching/CMPT362/code/Kotlin_code/BindDemoKotlin.zip
    // modified according to use case
    private fun displayNotification() {
        val intentNotification = Intent(this, MainActivity::class.java)
        intentNotification.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        // allows another application to access even if app is killed
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intentNotification,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            channelId
        )

        // Notification Display Values
        notificationBuilder.setSmallIcon(R.drawable.icons_bill)
        notificationBuilder.setContentTitle("TransXactShun")
        notificationBuilder.setContentText("Bill due soon, please check the app to see your bills")
        notificationBuilder.setContentIntent(pendingIntent)

        val notification = notificationBuilder.build()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            channelId,
            "channel name",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(notificationId, notification)
    }


    override fun onBind(p0: Intent?): IBinder? {
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun setmsgHandler(msgHandler: Handler) {
            this@BillReminderService.msgHandler = msgHandler
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        msgHandler = null
        return true
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}