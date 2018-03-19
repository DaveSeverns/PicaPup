package com.pic_a_pup.dev.pic_a_pup.Utilities

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pic_a_pup.dev.pic_a_pup.Controller.LoginActivity
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import java.net.URL

/**
 * Created by davidseverns on 3/16/18.
 */
class FirebaseManager(var mContext: Context, var mAuth: FirebaseAuth, var mDB : FirebaseDatabase, var mUserDBRef: DatabaseReference) : Utility(mContext){
    init{
        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseDatabase.getInstance()
        mUserDBRef = FirebaseDatabase.getInstance().reference.child(USER_TABLE)
    }

    fun getFeedList(dataSnapshot: DataSnapshot): ArrayList<Model.DogSearchResult>{
        var dogSearches = ArrayList<Model.DogSearchResult>()
        var result: Model.DogSearchResult

        for(search in dataSnapshot.children){
            if(search.exists()){

                result = Model.DogSearchResult(search.child("userWhoSearched").value.toString(),search.child("dog").value as Model.Dog,
                            search.child("dogImageSent").value as URL, search.child("userVoteCount").value as Int,
                            search.child("shelterList").value as List<Model.DogShelter>)
                dogSearches.add(result)

            }
        }
        return dogSearches
    }

}