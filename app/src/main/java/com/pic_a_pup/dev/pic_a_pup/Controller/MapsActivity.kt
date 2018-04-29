package com.pic_a_pup.dev.pic_a_pup.Controller

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabPark: FloatingActionButton
    private lateinit var fabStore: FloatingActionButton
    private lateinit var fabVet: FloatingActionButton
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private var isFabOpen: Boolean = false
    private var placesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
    private var searchId: Int? = null
    private var queryPlaceType: String? = null
    private var queryPlaceKeyword: String? = null
    private var queryRadius = 16000
    private var latFromIntent: Double?=null
    private var lonFromIntent: Double? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private var mImagePath: String? = null
    private lateinit var mUtility: Utility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                10)

        setUpNavBar()

        if(intent != null){
            latFromIntent = intent!!.getDoubleExtra("lat",39.9813235)
            lonFromIntent = intent!!.getDoubleExtra("lon",-75.1541054)


        }

        fabMain = findViewById(R.id.fab_main)
        fabPark = findViewById(R.id.fab_park)
        fabStore = findViewById(R.id.fab_store)
        fabVet = findViewById(R.id.fab_vet)

        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)

        fabAnimationSetup()

        fabPark.setOnClickListener {
            searchId = 1
            queryPlaceType = "park"
            queryPlaceKeyword = "dog"
            Toast.makeText(applicationContext, "Searching for Dog Parks", Toast.LENGTH_LONG).show()
            googlePlacesQuery(searchId!!, queryPlaceType!!, queryPlaceKeyword!!, queryRadius)
        }

        fabStore.setOnClickListener {
            searchId = 2
            queryPlaceType = "store"
            queryPlaceKeyword = "pet"
            Toast.makeText(applicationContext, "Searching for Pet Stores", Toast.LENGTH_LONG).show()
            googlePlacesQuery(searchId!!, queryPlaceType!!, queryPlaceKeyword!!, queryRadius)
        }

        fabVet.setOnClickListener {
            searchId = 3
            queryPlaceType = "veterinary_care"
            queryPlaceKeyword = "dog"
            Toast.makeText(applicationContext, "Searching for Vets", Toast.LENGTH_LONG).show()
            googlePlacesQuery(searchId!!, queryPlaceType!!, queryPlaceKeyword!!, queryRadius)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun fabAnimationSetup() {
        fabMain.setOnClickListener {
            if (isFabOpen) {
                fabPark.startAnimation(fabClose)
                fabStore.startAnimation(fabClose)
                fabVet.startAnimation(fabClose)

                fabPark.isClickable = false
                fabStore.isClickable = false
                fabVet.isClickable = false

                isFabOpen = false
            } else {
                fabPark.startAnimation(fabOpen)
                fabStore.startAnimation(fabOpen)
                fabVet.startAnimation(fabOpen)

                fabPark.isClickable = true
                fabStore.isClickable = true
                fabVet.isClickable = true

                isFabOpen = true
            }
        }
    }

    private fun setUpNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_map_page)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(1)
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

        navigation_map_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun googlePlacesQuery(id: Int, type: String, keyword: String, radius: Int) {
        var query = "&location=${lastLocation.latitude},${lastLocation.longitude}" +
            "+&radius=$radius&type=$type&keyword=$keyword"
        query = "$placesUrl$query&key=$GOOGLE_PLACES_KEY"

        val request = Request.Builder().url(query).build()
        val client = OkHttpClient()
        val zoomTo = 11.0f

        when(id) {
            1 -> {
                map.clear()
                client.newCall(request).enqueue(object: Callback {
                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        val gson = GsonBuilder().create()
                        val data = gson.fromJson(body, Model.Data::class.java)

                        runOnUiThread {
                            for (i in data.results.indices) {
                                val latitude = data.results[i].geometry.location.lat
                                val longitude = data.results[i].geometry.location.lng
                                val name = data.results[i].name
                                val address = data.results[i].vicinity
                                val parkLatLng = LatLng(latitude, longitude)

                                placeMarkerOnMap(parkLatLng, name, address)
                                map.animateCamera(CameraUpdateFactory.zoomTo(zoomTo))
                            }
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        println("Failed")
                    }
                })
            }
            2 -> {
                map.clear()
                client.newCall(request).enqueue(object: Callback {
                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        val gson = GsonBuilder().create()
                        val data = gson.fromJson(body, Model.Data::class.java)

                        runOnUiThread {
                            for (i in data.results.indices) {
                                val latitude = data.results[i].geometry.location.lat
                                val longitude = data.results[i].geometry.location.lng
                                val name = data.results[i].name
                                val address = data.results[i].vicinity
                                val parkLatLng = LatLng(latitude, longitude)


                                placeMarkerOnMap(parkLatLng, name, address)
                                map.animateCamera(CameraUpdateFactory.zoomTo(zoomTo))
                            }
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        println("Failed")
                    }
                })
            }
            3 -> {
                map.clear()
                client.newCall(request).enqueue(object: Callback {
                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        val gson = GsonBuilder().create()
                        val data = gson.fromJson(body, Model.Data::class.java)

                        runOnUiThread {
                            for (i in data.results.indices) {
                                val latitude = data.results[i].geometry.location.lat
                                val longitude = data.results[i].geometry.location.lng
                                val name = data.results[i].name
                                val address = data.results[i].vicinity
                                val parkLatLng = LatLng(latitude, longitude)

                                placeMarkerOnMap(parkLatLng, name, address)
                                map.animateCamera(CameraUpdateFactory.zoomTo(zoomTo))
                            }
                        }
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        println("Failed")
                    }
                })
            }
        }

    }

    private fun placeMarkerOnMap(location: LatLng, name: String, address: String) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(name)
            .snippet(address)

        map.addMarker(markerOptions)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = false
        map.setOnMarkerClickListener(this)

        if(latFromIntent != null && lonFromIntent !=  null){
            val latLng = LatLng(latFromIntent!!,lonFromIntent!!)
            placeMarkerOnMap(latLng,"Dog Found Here", "Finder Should Text You")
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f))
        }

        setupMap()
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun setupMap() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)

            return
        }

        map.isMyLocationEnabled = true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }
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

    private fun onLaunchCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
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

        val file = File(mImagePath!!)
        val fileUri = FileProvider.getUriForFile(this,getString(R.string.file_provider_authority),file)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(cameraIntent, REQUEST_IMG_CAPTURE)

    }

    private fun onOpenGallery(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
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

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }



    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MapsActivity::class.java)
        }
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val GOOGLE_PLACES_KEY = "AIzaSyAxtwhf8egj3eThPnZHIr8HwWcqbd80FuQ"
    }
}