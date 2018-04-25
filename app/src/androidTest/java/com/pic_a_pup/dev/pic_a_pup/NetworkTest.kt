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
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.*
import okhttp3.ResponseBody
import org.json.JSONObject
import org.json.JSONTokener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.security.auth.callback.Callback

private const val urlString = "https://firebasestorage.googleapis.com/v0/b/pic-a-pup.appspot.com/o/PupImages%2Fandroid.graphics.Bitmap%40631b8d4?alt=media&token=9f513043-b1a4-4d15-8ed0-3565a3cc8dd6"

/**
 * Created by davidseverns on 3/18/18.
 */
@RunWith(AndroidJUnit4::class)
class NetworkTest{

    private lateinit var networkManager: NetworkManager
    private lateinit var paPRestClient: NetworkManager.PaPRestClient

    @Before
    fun setup(){
        networkManager = NetworkManager()
        paPRestClient = NetworkManager.PaPRestClient.create()
    }

    @Test
    fun testPostSearchToServer(){
        var aResponse: String? = null

        paPRestClient.postSearchRequestToServer(urlString,"19122").enqueue(object : retrofit2.Callback<Model.DogSearchResult>{
            override fun onFailure(call: Call<Model.DogSearchResult>?, t: Throwable?) {
                assertNotNull(null)
            }

            override fun onResponse(call: Call<Model.DogSearchResult>?, response: Response<Model.DogSearchResult>?) {
                assertNotNull(response!!.body())
            }


        })
        //val client = NetworkManager.PaPRestClient.create()
        //val disposable = Observable.subscribeOn(Schedulers.io())



    }
}
