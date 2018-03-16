package com.pic_a_pup.dev.pic_a_pup.Utilities

import android.content.Context
import android.location.Location
import android.widget.Toast

/**
 * Created by davidseverns on 3/15/18.
 */
class Utility(private var mContext: Context) {
    fun isValidEmail(email: CharSequence): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun showToast(toastString: String){
        Toast.makeText(mContext,toastString,Toast.LENGTH_SHORT).show()
    }
}