package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.pic_a_pup.dev.pic_a_pup.DB.DbManager
import com.pic_a_pup.dev.pic_a_pup.Adapters.DogRecyclerAdapter
import com.pic_a_pup.dev.pic_a_pup.Model.DogLover
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dog_add_dialog.*
import java.util.*
import kotlin.collections.ArrayList

class ProfileActivity : AppCompatActivity(), DogRecyclerAdapter.LostDogSwitchListener {



    private val mFirebaseManager = FirebaseManager(this)
    private var mUserDb: FirebaseDatabase? = null
    private lateinit var dogListAdapter: DogRecyclerAdapter
    private var userName: String? = null
    private var phoneNumber:String? = null
    private var fcmToken:String? = null
    private lateinit var mDbManager: DbManager
    private lateinit var dogsList: ArrayList<Model.Dog>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val sharedPreferences = getSharedPreferences(USER_PREF_FILE, Context.MODE_PRIVATE)
        userName = sharedPreferences.getString(PREF_USER_NAME_KEY,"default")
        phoneNumber = sharedPreferences.getString(PREF_USER_PHONE_KEY, "2813308004")
        fcmToken = sharedPreferences.getString(FCM_TOKEN_PREF_KEY,"fcm_token_here")
        mDbManager = DbManager(applicationContext)
        dogsList = mDbManager.getDoggosFromDb()

        doglover_name.text = userName
        doglover_phone.text = phoneNumber

        dogListAdapter = DogRecyclerAdapter(this, dogsList,this)
        dog_recycler.adapter = dogListAdapter
        val layoutManager = LinearLayoutManager(this)
        dog_recycler.layoutManager = layoutManager
        add_dog_btn.setOnClickListener(this::addDogClicked)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_profile_page)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(4)
        menuItem.isChecked = true

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        val intentHome = HomeFeedActivity.newIntent(this)
                        startActivity(intentHome)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_map -> {
                        val intentMap = MapsActivity.newIntent(this)
                        startActivity(intentMap)
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

    fun postLostDog(dog: Model.Dog){
        val user = DogLover(fcmToken,null,userName, null, phoneNumber, null)
        val lostDog = Model.LostDog(dog.dogName,user,"fcm")
        mFirebaseManager.mLostDogDBRef.child(dog.pupCode).setValue(lostDog)
    }

    override fun switchChanged(dog: Model.Dog) {
        postLostDog(dog)
        mFirebaseManager.showToast("Switch Changed")

    }



    fun generatePupCode(userName: String): String{
        var uuidString = UUID.randomUUID().toString().removeRange(4,31)
        var pupCode = "$uuidString-$userName"
        return pupCode
    }

    fun addDogClicked(view: View){
        val layoutInflater = getLayoutInflater()
        val dialogView = layoutInflater.inflate(R.layout.dog_add_dialog,null)
        val dogNameText = dialogView.findViewById<EditText>(R.id.add_dog_name)
        val dogBreedText = dialogView.findViewById<EditText>(R.id.add_dog_breed)

        AlertDialog.Builder(this)
                .setTitle("Add New Dog")
                .setView(dialogView)
                .setPositiveButton("Add",DialogInterface.OnClickListener({dialog: DialogInterface?, which: Int ->
                    var dogName = dogNameText.text.toString()
                    var dogBreed = dogBreedText.text.toString()
                    var pupCode = generatePupCode(userName!!)

                    val dogToAdd = Model.Dog(dogName,dogBreed,pupCode)
                    mDbManager.addDogToDb(dogToAdd)
                    // dog is added to db so persists but this add it in RT
                    dogsList.add(dogToAdd)
                    dogListAdapter.notifyDataSetChanged()

                })).show()
    }

    override fun longClicked(dog: Model.Dog) {



        Toast.makeText(this,"Long Clicked", Toast.LENGTH_LONG).show()
        dogsList.remove(dog)
        dogListAdapter.notifyDataSetChanged()
    }
}
