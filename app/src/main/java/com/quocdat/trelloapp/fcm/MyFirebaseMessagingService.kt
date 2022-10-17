package com.quocdat.trelloapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.activities.MainActivity
import com.quocdat.trelloapp.activities.SignInActivity
import com.quocdat.trelloapp.firebase.FireStoreClass
import com.quocdat.trelloapp.utils.Constants
import java.nio.file.attribute.AclEntry.Builder

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.i(TAG, "FROM: ${message.from}")

        message.data.isNotEmpty().let {
            Log.i(TAG, "Message data Payload: ${message.data}")

            val title = message.data[Constants.FCM_KEY_TITLE]!!
            val messageNotification = message.data[Constants.FCM_KEY_MESSAGE]!!

            sendNotification(title, messageNotification)
        }



        message.notification?.let {
            Log.i(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Refreshed token: $token ")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?){
        val sharedPreferences = this.getSharedPreferences(Constants.TRELLO_PREFERENCE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.FCM_TOKEN, token)
        editor.apply()
    }

    private fun sendNotification(title: String, message: String){
        val intent =
            if (FireStoreClass().getCurrentUserID().isNotEmpty()){
                Intent(this, MainActivity::class.java)
            }else{
                Intent(this, SignInActivity::class.java)
            }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(this,
            0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        ).setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
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