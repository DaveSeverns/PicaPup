package com.pic_a_pup.dev.pic_a_pup.Controller

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pic_a_pup.dev.pic_a_pup.DB.DbManager
import com.pic_a_pup.dev.pic_a_pup.Adapters.DogRecyclerAdapter
import com.pic_a_pup.dev.pic_a_pup.Model.DogLover
import com.pic_a_pup.dev.pic_a_pup.Model.FeedDogSearchResult
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dog_add_dialog.*
import java.io.File
import java.text.SimpleDateFormat
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

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private var mImagePath: String? = null
    private lateinit var mUtility: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                10)

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
                            getImageAlertDialog()
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        dogsList.remove(dog)
        dogListAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMG_CAPTURE && resultCode == Activity.RESULT_OK){
            val mImageFile= File(mImagePath!!)
            if(mImageFile.exists()){
                if(mLocation != null){
                    val classificationIntent = Intent(this, ClassificationActivity::class.java)
                    classificationIntent.putExtra(IMAGE_INTENT_TAG, mImageFile.absolutePath)
                    classificationIntent.putExtra(LAT_INTENT_TAG, mLocation!!.latitude)
                    classificationIntent.putExtra(LON_INTENT_TAG, mLocation!!.longitude)

                    startActivity(classificationIntent)
                }
                else{
                    //if location is null set default location to TempleUni
                    mUtility.showToast("Location needs to be on.")
                    val classificationIntent = Intent(this, ClassificationActivity::class.java)
                    classificationIntent.putExtra(IMAGE_INTENT_TAG, mImageFile.absolutePath)
                    classificationIntent.putExtra(LAT_INTENT_TAG, "39.9813235")
                    classificationIntent.putExtra(LON_INTENT_TAG, "-75.1541054")

                    startActivity(classificationIntent)
                }
            }
        }else if(requestCode == 42069 && resultCode == Activity.RESULT_OK){
            val targetUri = data!!.data
            val classificationIntent = Intent(this, ClassificationActivity::class.java)

            classificationIntent.putExtra(GALLERY_INTENT_TAG, targetUri)
            classificationIntent.putExtra(LAT_INTENT_TAG, mLocation!!.latitude)
            classificationIntent.putExtra(LON_INTENT_TAG, mLocation!!.longitude)

            startActivity(classificationIntent)
        }
    }

    fun onLaunchCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ProfileActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
                return
            }

        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                mLocation = location
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "$timeStamp.png"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        mImagePath = "${storageDir.absolutePath}/$imageFileName"
        //content://
        val file = File(mImagePath!!)
        val fileUri = FileProvider.getUriForFile(this,getString(R.string.file_provider_authority),file)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(cameraIntent, REQUEST_IMG_CAPTURE)
    }

    fun onOpenGallery(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ProfileActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
                return
            }

        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                mLocation = location
            }
        }
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent,42069)
    }

    private fun getImageAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.image_select_dialog, null)
        dialogBuilder.setView(dialogView)

        val uploadBtn = dialogView.findViewById(R.id.button_dialog_upload) as Button
        val camBtn = dialogView.findViewById(R.id.button_dialog_camera) as Button

        uploadBtn.setOnClickListener {
            onOpenGallery()
        }

        camBtn.setOnClickListener {
            onLaunchCamera()
        }

        dialogBuilder.create().show()
    }
}