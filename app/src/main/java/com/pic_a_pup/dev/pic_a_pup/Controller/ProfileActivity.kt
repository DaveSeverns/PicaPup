package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.pic_a_pup.dev.pic_a_pup.Model.DogLover
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.Utilities.BottomNavigationViewHelper
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.FirebaseManager
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var mockDog = Model.Dog("Spot", "Golden Retriever", "42069")
    private val mFirebaseManager = FirebaseManager(this)
    private var mUserName: String? = null
    private var mUserPhoneNumber: String? = null
    private var lostDog: Model.LostDog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val firebaseUser = mFirebaseManager.mAuth.currentUser
        if (firebaseUser != null){
            mFirebaseManager.mUserDBRef.equalTo(firebaseUser.uid).addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                    mUserPhoneNumber = dataSnapshot!!.child("phoneNumber").value.toString()
                    mUserName = dataSnapshot.child("username").value.toString()
                    Log.e("User",mUserName)

                }

                override fun onChildRemoved(p0: DataSnapshot?) {
                }

            })

        }




        val textView = findViewById<TextView>(R.id.testText)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_profile_page)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(3)
        menuItem.isChecked = true

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        val intentHome = HomeFeedActivity.newIntent(this)
                        startActivity(intentHome)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_camera -> {
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_collar ->{
                        val collarStartIntent = Intent(this, QRCollarActivity::class.java)
                        startActivity(collarStartIntent)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_profile -> {
                        val intentProfile = ProfileActivity.newIntent(this)
                        startActivity(intentProfile)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

        lost_dog_test.setOnClickListener(this::postLostDog)

        navigation_profile_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }

    fun postLostDog(view: View){
        lostDog = Model.LostDog(mockDog.pupCode,mockDog.dogName,mUserName,mUserPhoneNumber)
        mFirebaseManager.mLostDogDBRef.push().setValue(lostDog)
    }
}
