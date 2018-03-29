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
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Response
import java.util.*
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

        //val client = NetworkManager.PaPRestClient.create()
        //val disposable = Observable.subscribeOn(Schedulers.io())

        assertNotNull(aResponse)

    }
}
