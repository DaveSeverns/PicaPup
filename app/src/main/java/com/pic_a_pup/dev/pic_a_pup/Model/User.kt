package com.pic_a_pup.dev.pic_a_pup.Model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL

/**
 * Created by davidseverns on 3/14/18.
 */
open class User(var userId: String, var name: String, var image: URL?) : Parcelable, Lovable {
    override fun showDogsSomeLove(doggo: Model.Dog): Model.Dog {
        //TODO needs to change the userVoteCount in a search Object
        return doggo
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            TODO("image")) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}