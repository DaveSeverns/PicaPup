package com.pic_a_pup.dev.pic_a_pup.Utilities

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import org.json.JSONObject
import java.util.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter

const val EMAIL_PASS_SUCCESS = 1
const val EMAIL_ONLY_SUCCESS = 2
const val PASS_ONLY_SUCCESS = 3
const val BOTH_FAIL = 0

/**
 * Created by davidseverns on 3/15/18.
 */
open class Utility(private var mContext: Context) {



    fun signUpVerification(email: CharSequence, password: String?){
        var x: Int
        val emailVerify = isValidEmail(email)
        val passVerify = isValidPassword(password)
        if(emailVerify && passVerify){
            x = EMAIL_PASS_SUCCESS
        }else if(emailVerify && !passVerify){
            x = EMAIL_ONLY_SUCCESS
        }
        else if (!emailVerify && passVerify){
            x = PASS_ONLY_SUCCESS
        }
        else{
            x = BOTH_FAIL
        }

        when(x){
            EMAIL_PASS_SUCCESS -> showToast("Login Successful")

            EMAIL_ONLY_SUCCESS -> showToast( "Invalid Password")

            PASS_ONLY_SUCCESS -> showToast("Invalid Email")

            BOTH_FAIL -> showToast("Please enter a valid Email and Password")
        }
    }


    fun isValidEmail(email: CharSequence): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /*
     * Between 6 and 21 characters
     * A password must contain at least 1 digit and 1 special character
     */
    fun isValidPassword(password: String?): Boolean{
        var special = false
        var digit = false
        var upperCase = false
        var lowerCase = false

        if (password != null && password.length > 5 && password.length < 21) {
            var index = 0
            while(index < password.length) {
                val character = password[index]
                if (Character.isWhitespace(character))
                    return false
                else if (Character.isLowerCase(character))
                    lowerCase = true
                else if (Character.isUpperCase(character))
                    upperCase = true
                else if (Character.isDigit(character))
                    digit = true
                else if (character.toInt() in 33..127)
                    special = true
                index++
            }
        }
        return special && digit && upperCase && lowerCase
    }


    //abstraction of android's toast function, QOL change
    fun showToast(toastString: String){
        Toast.makeText(mContext,toastString,Toast.LENGTH_SHORT).show()
    }

    fun convertToJSON(any: Any): JSONObject{
        val ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        return JSONObject(ow.writeValueAsString(any))
    }

    fun getZipFromLatLon(lat: String, lon: String): String{
        // geocoder object gets addresses from specified locale
        val geoCoder = Geocoder(mContext, Locale.US)
        var addresses : List<Address> = geoCoder.getFromLocation(lat.toDouble(),lon.toDouble(),1)
        return addresses.get(0).postalCode

    }
}