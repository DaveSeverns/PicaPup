package com.pic_a_pup.dev.pic_a_pup.Model

import java.net.URL

/**
 * Created by davidseverns on 3/14/18.
 */
class Admin(fcm_id: String?,userId: String?, name: String?, image: URL?, var isAdmin: Boolean): User(fcm_id,userId,name,image) {
    fun deletePost(): Boolean{
        return false
    }

    fun blackListUser(): Boolean{
        return false
    }

    fun isUserAdmin(): Boolean{
        return isAdmin
    }
}