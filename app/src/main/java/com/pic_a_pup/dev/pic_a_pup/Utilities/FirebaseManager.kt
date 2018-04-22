package com.pic_a_pup.dev.pic_a_pup.Utilities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.pic_a_pup.dev.pic_a_pup.Controller.LoginActivity
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import java.io.File
import java.net.URL

/**
 * Created by davidseverns on 3/16/18.
 */
class FirebaseManager(var mContext: Context) : Utility(mContext) {
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mDB: FirebaseDatabase = FirebaseDatabase.getInstance()
    var mUserDBRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(USER_TABLE)
    var mStorageReference: StorageReference = FirebaseStorage.getInstance().reference
    var mResultDBRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(RESULTS_TABLE)
    var mLostDogDBRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child(LOST_DOG_TABLE)




    fun getFeedList(dataSnapshot: DataSnapshot): ArrayList<Model.DogSearchResult> {
        var dogSearches = ArrayList<Model.DogSearchResult>()
        var result: Model.DogSearchResult

        for (search in dataSnapshot.children) {
            if (search.exists()) {

                //result = Model.DogSearchResult(search.child("userWhoSearched").value.toString(), search.child("dog").value as Model.Dog,
                       // search.child("dogImageSent").value as URL, search.child("userVoteCount").value as Int,
                        //search.child("shelterContact").value as List<Model.DogShelter>)
               // dogSearches.add(result)

            }
        }
        return dogSearches
    }

    fun logUserIntoFirebase(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { _ -> showToast("Entered!") }
                .addOnSuccessListener { _ -> showToast("Login Successful") }
                .addOnFailureListener { _  -> showToast("Invalid Username or Password") }
    }

    fun postImageToFireBaseForUrl(imageBitmap: Bitmap, mImageFile: File): String {
        val filePath = mStorageReference.child(IMAGE_STORAGE).child(imageBitmap.toString())
        val fileUri = Uri.fromFile(mImageFile)
        var imgUrl: Uri? = null

        filePath.putFile(fileUri).addOnSuccessListener {
            object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                    imgUrl = taskSnapshot.downloadUrl
                    showToast(imgUrl.toString())
                    Log.e("Fb Mngr", "Success")
                }
            }

        }
        while(imgUrl == null){}
        return imgUrl.toString()
    }


}




