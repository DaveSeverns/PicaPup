package com.pic_a_pup.dev.pic_a_pup.Utilities

import com.pic_a_pup.dev.pic_a_pup.Model.Model
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by davidseverns on 3/16/18.
 */
class NetworkManager {
    interface PaPRestClient{
        @POST("./")
        @FormUrlEncoded
        fun postSearchRequestToServer(@Field("location") urlAsString: String?,
                                      @Field("url") postalLocation: String?): Call<ResponseBody>
        companion object Factory{
            fun create(): PaPRestClient{
                val retrofit = Retrofit.Builder().baseUrl(AWS_IP)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                return retrofit.create(PaPRestClient::class.java)
            }
        }
    }


}