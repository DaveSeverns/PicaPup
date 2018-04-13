package com.pic_a_pup.dev.pic_a_pup.FirebaseServices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PaPFirebaseMessagingService :  FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        super.onMessageReceived(remoteMessage)
    }
}
