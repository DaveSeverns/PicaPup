package com.pic_a_pup.dev.pic_a_pup.Model

import java.net.URL

/**
 * Created by davidseverns on 3/14/18.
 */
class DogLover(userId:String, name: String, image: URL?, var dogList: List<Model.Dog>?):User(userId,name,image) {
    fun getDogFromList(pupCode: String): Model.Dog?{
        return null
    }
}