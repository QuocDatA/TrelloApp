package com.quocdat.trelloapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.activities.MainActivity
import java.nio.file.attribute.AclEntry.Builder

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.i("CheckData: ", "FROM: ${message.from}")

        message.data.isNotEmpty().let {
            Log.i(TAG, "Message data Payload: ${message.data}")
        }

        message.notification?.let {
            Log.i(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Refreshed token: $token ")
    }

    private fun sendRegistrationToServer(token: String?){

    }

    private fun sendNotification(messageBody: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,
            0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        ).setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Title")
            .setContentText("Message")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                channelId, "Channel Trello Title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object{
        private const val TAG = "MyFirebaseMsgService"
    }
}