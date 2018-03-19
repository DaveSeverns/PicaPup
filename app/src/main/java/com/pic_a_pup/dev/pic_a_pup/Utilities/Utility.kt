package com.pic_a_pup.dev.pic_a_pup.Utilities

import android.content.Context
import android.location.Location
import android.widget.Toast
import org.json.JSONObject
import java.util.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter

/**
 * Created by davidseverns on 3/15/18.
 */
open class Utility(private var mContext: Context) {


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
        var ow = ObjectMapper().writer().withDefaultPrettyPrinter()
        return JSONObject(ow.writeValueAsString(any))
    }
}