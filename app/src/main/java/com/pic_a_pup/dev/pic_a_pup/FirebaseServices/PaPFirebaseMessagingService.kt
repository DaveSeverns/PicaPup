package com.pic_a_pup.dev.pic_a_pup.FirebaseServices

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.media.RingtoneManager
import com.pic_a_pup.dev.pic_a_pup.R
import java.util.*


class PaPFirebaseMessagingService :  FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("MESSAGE FROM", remoteMessage!!.from)
        if((remoteMessage?.data!!.isNotEmpty()) && (remoteMessage.notification != null)){
            Log.e("Message Payload", remoteMessage.data.toString())

        }


        //super.onMessageReceived(remoteMessage)
    }
}
