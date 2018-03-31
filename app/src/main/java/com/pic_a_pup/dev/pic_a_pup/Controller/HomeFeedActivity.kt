package com.pic_a_pup.dev.pic_a_pup.Controller

import android.Manifest
import android.app.Activity
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_home_feed.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.content.FileProvider.getUriForFile
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
//import com.firebase.ui.database.FirebaseRecyclerAdapter
//import com.firebase.ui.database.FirebaseRecyclerOptions
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import java.security.AccessController.getContext


class HomeFeedActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private var mImagePath: String? = null
    private lateinit var mUtility: Utility
    //private lateinit var adapter: FirebaseRecyclerAdapter<Model.DogSearchResult,ResultViewHolder>

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_camera -> {
                onLaunchCamera()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_collar ->{
                collarActivityStart()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_feed)

        //request the necessary permissions
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                10)

        mAuth = FirebaseAuth.getInstance()
        mUtility = Utility(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onStart() {
        super.onStart()
        var currentUser = mAuth.currentUser
        Log.e("CURRENT_USER",currentUser.toString())
        //adapter =




    }


    fun onLaunchCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@HomeFeedActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if(id == R.id.log_out_button){
            mAuth!!.signOut()
            val logOutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logOutIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

    }

    fun collarActivityStart(){
        val collarStartIntent = Intent(this, QRCollarActivity::class.java)
        startActivity(collarStartIntent)
    }




}
