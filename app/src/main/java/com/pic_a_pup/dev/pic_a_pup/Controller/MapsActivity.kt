package com.pic_a_pup.dev.pic_a_pup.Controller

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabPark: FloatingActionButton
    private lateinit var fabStore: FloatingActionButton
    private lateinit var fabVet: FloatingActionButton
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private var isFabOpen: Boolean = false
    private var placesUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
    private var DogParks = ArrayList<Model.DogPark>()
    private val queryPlaceKeyword = "dog"
    private val queryPlaceType = "park"
    private val radius = 16000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val fabMain = findViewById<FloatingActionButton>(R.id.fab_main)
        val fabPark = findViewById<FloatingActionButton>(R.id.fab_park)
        val fabStore = findViewById<FloatingActionButton>(R.id.fab_store)
        val fabVet = findViewById<FloatingActionButton>(R.id.fab_vet)

        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)

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

        fabPark.setOnClickListener {
            googlePlacesQuery()
        }

        fabStore.setOnClickListener {
            Toast.makeText(applicationContext, "Searching for pet stores", Toast.LENGTH_LONG).show()
        }

        fabVet.setOnClickListener {
            Toast.makeText(applicationContext, "Searching for Vets", Toast.LENGTH_LONG).show()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                map.clear()

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        createLocationRequest()
    }

    private fun googlePlacesQuery() {
        var query = "&location=${lastLocation.latitude},${lastLocation.longitude}" +
            "+&radius=$radius&type=$queryPlaceType&keyword=$queryPlaceKeyword"
        query = "$placesUrl$query&key=$GOOGLE_PLACES_KEY"

        Fuel.get(query)
            .responseJson { request, response, result ->
                Log.e("Result", result.get().content)
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = false
        map.setOnMarkerClickListener(this)

        //Call placeMarkerOnMap and include a loop to add available tutor pins to the map
//        val placeHolder = LatLng(39.980944, -75.157837)
//        map.addMarker(MarkerOptions().position(placeHolder).title("Tutor Shulmoney"))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeHolder, 12.0f))

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

                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        map.addMarker(markerOptions)
    }

    private fun locationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @SuppressLint("RestrictedApi")
    private fun createLocationRequest() {
        locationRequest = LocationRequest()

        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            locationUpdates()
        }
        task.addOnFailureListener {e ->
            if(e is ResolvableApiException)
                try{
                    e.startResolutionForResult(this@MapsActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
        }
    }

//    private fun loadPlacePicker() {
//        val builder = PlacePicker.IntentBuilder()
//
//        try {
//            startActivityForResult(builder.build(this@MapsActivity), PLACES_PICKER_REQUEST)
//        } catch (e: GooglePlayServicesNotAvailableException) {
//            e.printStackTrace()
//        } catch (e: GooglePlayServicesRepairableException) {
//            e.printStackTrace()
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CHECK_SETTINGS) {
            if(resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                locationUpdates()
            }
        }
//        if(requestCode == PLACES_PICKER_REQUEST) {
//            val place = PlacePicker.getPlace(this, data)
//            var addressText = place.name.toString()
//            addressText += "\n" + place.address.toString()
//
//            placeMarkerOnMap(place.latLng)
//        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if(!locationUpdateState) {
            locationUpdates()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MapsActivity::class.java)
        }
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
//        private const val PLACES_PICKER_REQUEST = 3
        private const val GOOGLE_PLACES_KEY = "AIzaSyAxtwhf8egj3eThPnZHIr8HwWcqbd80FuQ"
    }
}
