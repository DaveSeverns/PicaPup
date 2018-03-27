package com.pic_a_pup.dev.pic_a_pup

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.Model.User
import com.pic_a_pup.dev.pic_a_pup.Utilities.FirebaseManager
import com.pic_a_pup.dev.pic_a_pup.Utilities.NetworkManager
import com.pic_a_pup.dev.pic_a_pup.Utilities.Utility
import junit.framework.Assert.*
import okhttp3.ResponseBody
import org.json.JSONObject
import org.json.JSONTokener
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

        val mFirebaseManager = FirebaseManager(InstrumentationRegistry.getContext())

        val client = NetworkManager.PaPRestClient.create()
        client.postSearchRequestToServer("19044", "https://firebasestorage.googleapis.com/v0/b/pic-a-pup.appspot.com/o/PupImages%2F1521417989.67944.jpg?alt=media&token=9e427ac8-178b-4606-bbfb-2b14fcf26c55").enqueue(
                object: retrofit2.Callback<Model.DogSearchResult> {
                    override fun onResponse(call: Call<Model.DogSearchResult>?, response: Response<Model.DogSearchResult>?) {
                        Log.e("response", response!!.body()!!.breed)

                        var stringResponseBody : String = response.body()!!.toString()


                        //var jsonObject = JSONObject()
                        //var stringJson = jsonObject.optJSONArray(stringResponseBody)
                        //println(jsonObject.get("breed"))

                        aResponse = response.body()!!.toString()
                        assertNotNull(aResponse)

                    }

                    override fun onFailure(call: Call<Model.DogSearchResult>?, t: Throwable?) {
                        Log.e("Oh", " No")

                    }

                })

        assertNotNull(aResponse)

    }
}
