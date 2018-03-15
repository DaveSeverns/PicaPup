package com.pic_a_pup.dev.pic_a_pup.Model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL

/**
 * Created by davidseverns on 3/14/18.
 */
open class User(var userId: String, var name: String, var image: URL?) :  Lovable {
    override fun showDogsSomeLove(doggo: Model.Dog): Model.Dog {
        //TODO needs to change the userVoteCount in a search Object
        return doggo
    }



}