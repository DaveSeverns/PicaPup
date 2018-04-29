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
import android.app.PendingIntent
import android.media.RingtoneManager
import com.pic_a_pup.dev.pic_a_pup.Controller.MapsActivity
import com.pic_a_pup.dev.pic_a_pup.R
import java.util.*


class PaPFirebaseMessagingService :  FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("MESSAGE FROM", remoteMessage!!.from)
        if((remoteMessage?.data!!.isNotEmpty()) && (remoteMessage.notification != null)){

            val lat = remoteMessage.data.get("locationLat")
            val lon = remoteMessage.data.get("locationLon")
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("lat",lat)
            intent.putExtra("lon", lon)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 6969,intent,0)
            Log.e("Message Payload", remoteMessage.data.toString())
            var notification = Notification.Builder(applicationContext).setContentTitle(remoteMessage.notification!!.body)
                    .setContentText(remoteMessage.data.toString()).setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent).build()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1791, notification)

        }


        //super.onMessageReceived(remoteMessage)
    }
}
