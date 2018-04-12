package com.pic_a_pup.dev.pic_a_pup.Model

import android.location.Location
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

    data class DogSearchResult(var breed: String,
                               var breed_info: String,
                               var dog : Model.Dog?,
                               var dogImageSent: URL?,
                               var userVoteCount: Int?,
                               var model_error: String?,
                               var wikipedia_error: String?,
                               var petfinder_error: String?,
                               var shelterList: List<DogShelter>?): JSONObject(){

    }

    data class ModelSearchRequest(var imgUrl: String,
                                  var usePetfinder : Boolean,
                                  var useWiki: Boolean,
                                  var location: String)

    data class LostDog(var dogName: String?,
                       var dogLover: DogLover?)




}