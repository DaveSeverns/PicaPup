package com.pic_a_pup.dev.pic_a_pup.FirebaseServices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PaPFirebaseMessagingService :  FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        Log.e("MESSAGE FROM", remoteMessage!!.from)
        if(remoteMessage?.data!!.isNotEmpty()){
            Log.e("Message Payload", remoteMessage.data.toString())
        }

        if(remoteMessage.notification != null){
            Log.e("Notification Body ", remoteMessage.notification!!.body)
        }
        //super.onMessageReceived(remoteMessage)
    }
}
