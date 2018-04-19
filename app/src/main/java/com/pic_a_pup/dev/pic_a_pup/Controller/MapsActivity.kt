package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.pic_a_pup.dev.pic_a_pup.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

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
    private var queryRadius: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

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
            queryRadius = 16000
            Toast.makeText(applicationContext, "Searching for Dog Parks", Toast.LENGTH_LONG).show()
            googlePlacesQuery(searchId!!, queryPlaceType!!, queryPlaceKeyword!!, queryRadius!!)
        }

        fabStore.setOnClickListener {
            searchId = 2
            queryPlaceType = "store"
            queryPlaceKeyword = "pet"
            queryRadius = 16000
            Toast.makeText(applicationContext, "Searching for Pet Stores", Toast.LENGTH_LONG).show()
            googlePlacesQuery(searchId!!, queryPlaceType!!, queryPlaceKeyword!!, queryRadius!!)
        }

        fabVet.setOnClickListener {
            searchId = 3
            queryPlaceType = "veterinary_care"
            queryPlaceKeyword = "dog"
            queryRadius = 16000
            Toast.makeText(applicationContext, "Searching for Vets", Toast.LENGTH_LONG).show()
            googlePlacesQuery(searchId!!, queryPlaceType!!, queryPlaceKeyword!!, queryRadius!!)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
                client.newCall(request).enqueue(object: Callback{
                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        val gson = GsonBuilder().create()
                        val data = gson.fromJson(body, Data::class.java)

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
                client.newCall(request).enqueue(object: Callback{
                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        val gson = GsonBuilder().create()
                        val data = gson.fromJson(body, Data::class.java)

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
                client.newCall(request).enqueue(object: Callback{
                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        val gson = GsonBuilder().create()
                        val data = gson.fromJson(body, Data::class.java)

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

//                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
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

//Data classes for Google Places API search
class Data(val results: List<Result>)
class Result(val name: String, val vicinity: String, val geometry: Geometry)
class Geometry(var location: Locations)
class Locations(val lat: Double, val lng: Double)
