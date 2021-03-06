package com.pic_a_pup.dev.pic_a_pup.Model

import android.location.Location
import android.os.IBinder
import org.json.JSONObject
import java.net.URL

/**
 * Created by davidseverns on 3/14/18.
 */
object Model {
    data class DogShelter(var shelterName: String,
                          var shelterLocal: Location,
                          var address: String)

    data class Dog(var dogName: String,
                   var dogBreed: String?,
                   var pupCode: String,
                   var isLost: Boolean = false)

    data class DogPark(var parkName: String,
                       var parkLocation: Location,
                       var parkAddress: String)

    data class DogSearchResult(var breed: String?,
                               var breed_info: String?,
                               var name: String?,
                               var sex: String,
                               var dogImageSent: String?,
                               var model_error: String?,
                               var wikipedia_error: String?,
                               var petfinder_error: String?,
                               val shelter_contact: ShelterContact?,
                               var prob: Double?)


    class ShelterContact(val city: String,
                              val zip: String,
                              val phone: String,
                              val state: String)

    data class ModelSearchRequest(var imgUrl: String,
                                  var usePetfinder : Boolean,
                                  var useWiki: Boolean,
                                  var location: String)

    class Data(val results: List<Result>)

    class Result(val name: String,
                 val vicinity: String,
                 val geometry: Geometry)

    class Geometry(var location: Locations)

    class Locations(val lat: Double,
                    val lng: Double)

    data class LostDog(var dogName: String?,
                       var dogLover: DogLover?,
                       var fcm_id:String?,
                       var found: Boolean = false,
                       var latitude: Double?,
                       var longitude: Double?)

    data class FcmNotificationModel(var to:String?,
                                    var priority:String = "normal",
                                    var notification: HashMap<String,String>?,
                                    var data: HashMap<String,String>?){

        companion object FCMNotificationFactory {
            fun createFCMNotification(fcmTokenRecipient: String,
                                      finderName:String, finderPhone:String,
                                      finderLocation:Location): FcmNotificationModel{
                var notficationMap = HashMap<String,String>()
                notficationMap.put("body","$finderName found you dog!")
                notficationMap.put("title","Pic-a-Pup")
                notficationMap.put("icon","new")
                var dataMap = HashMap<String,String>()
                dataMap.put("lat", finderLocation.latitude.toString())
                dataMap.put("lon",finderLocation.longitude.toString())
                var fcmNotificationModel = FcmNotificationModel(fcmTokenRecipient,"normal",notficationMap,dataMap)
                return fcmNotificationModel
            }
        }
    }


}