package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ListAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.pic_a_pup.dev.pic_a_pup.DogRecyclerAdapter
import com.pic_a_pup.dev.pic_a_pup.Model.DogLover
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var mockDog = Model.Dog("Spot", "Golden Retriever", "42069")
    private var mockDogTwo = Model.Dog("Rex", "Golden Doodle", "Vap3N4ych")
    private val mFirebaseManager = FirebaseManager(this)
    private var mUserDb: FirebaseDatabase? = null
    private lateinit var dogListAdapter: DogRecyclerAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val dogsTest = listOf<Model.Dog>(mockDog,mockDogTwo)

        dogListAdapter = DogRecyclerAdapter(this, dogsTest)
        dog_recycler.adapter = dogListAdapter
        val layoutManager = LinearLayoutManager(this)
        dog_recycler.layoutManager = layoutManager





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


        navigation_profile_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }

    fun postLostDog(view: View){
        val prefs = getSharedPreferences(USER_PREF_FILE,Context.MODE_PRIVATE)

        val mUserName = prefs.getString(PREF_USER_NAME_KEY,"default_user")
        val mUserPhoneNumber = prefs.getString(PREF_USER_PHONE_KEY,"1111111111")
        var lostDog = Model.LostDog(mockDog.pupCode,mockDog.dogName,mUserName,mUserPhoneNumber)
        mFirebaseManager.mLostDogDBRef.push().setValue(lostDog)


    }

    override fun onStart() {
        super.onStart()
    }
}
