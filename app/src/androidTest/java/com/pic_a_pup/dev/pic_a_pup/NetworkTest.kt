package com.pic_a_pup.dev.pic_a_pup

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.Model.User
import com.pic_a_pup.dev.pic_a_pup.Utilities.NetworkManager
import com.pic_a_pup.dev.pic_a_pup.Utilities.Utility
import junit.framework.Assert.*
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

/**
 * Created by davidseverns on 3/18/18.
 */
@RunWith(AndroidJUnit4::class)
class NetworkTest{
    @Test
    fun doesNetworkMangerWork(){
        var aResponse: String? = null

        val cliet = NetworkManager.PaPRestClient.create()
        cliet.postSearchRequestToServer("Url", "Location").enqueue(
                object: retrofit2.Callback<JSONObject> {
                    override fun onResponse(call: Call<JSONObject>?, response: Response<JSONObject>?) {
                        Log.e("response", response!!.headers().toString())

                        aResponse = response.toString()
                    }

                    override fun onFailure(call: Call<JSONObject>?, t: Throwable?) {
                        Log.e("Oh", " No")
                    }

                })

        assertNotNull(aResponse)
    }
}
