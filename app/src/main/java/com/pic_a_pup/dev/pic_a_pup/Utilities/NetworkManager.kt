package com.pic_a_pup.dev.pic_a_pup.Utilities

import com.pic_a_pup.dev.pic_a_pup.Model.Model
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by davidseverns on 3/16/18.
 */
class NetworkManager {
    interface PaPRestClient{
        @POST(EMPTY_ROUTE)
        @FormUrlEncoded
        fun postSearchRequestToServer(@Field("location") urlAsString: String?,
                                      @Field("url") postalLocation: String?): Call<Model.DogSearchResult>
        companion object Factory{
            fun create(): PaPRestClient{
                val okHttpClient = OkHttpClient.Builder()
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .connectTimeout(120, TimeUnit.SECONDS)

                        //.addNetworkInterceptor(networkInterceptor)
                        .build()
                val retrofit = Retrofit.Builder().baseUrl(AWS_IP).client(okHttpClient)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                return retrofit.create(PaPRestClient::class.java)
            }
        }
    }

    interface FirebaseFCMNotificationClient{

        @POST(FCM_ENDPOINT)
        @Headers("Content-Type: $FIREBASE_CONTENT_TYPE",
            "Authorization: key=$FIREBASE_LEGACY_SERVER_KEY")
        fun postMessageToFCM(@Body body:Model.FcmNotificationModel) : Call<Void>

        companion object Factory{
            fun create(): FirebaseFCMNotificationClient{
                val retrofit = Retrofit.Builder().baseUrl(FIREBASE_BASE_URL)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                return retrofit.create(FirebaseFCMNotificationClient::class.java)
            }
        }
    }
}