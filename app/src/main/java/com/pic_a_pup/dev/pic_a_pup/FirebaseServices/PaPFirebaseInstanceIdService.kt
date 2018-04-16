package com.pic_a_pup.dev.pic_a_pup.FirebaseServices

import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.pic_a_pup.dev.pic_a_pup.Utilities.FCM_TOKEN_PREF_KEY
import com.pic_a_pup.dev.pic_a_pup.Utilities.USER_PREF_FILE

class PaPFirebaseInstanceIdService: FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val prefs = getSharedPreferences(USER_PREF_FILE,Context.MODE_PRIVATE)
        val editor = prefs.edit()

        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        editor.putString(FCM_TOKEN_PREF_KEY,refreshedToken).apply()
        Log.e("Token: ", refreshedToken)
    }
}