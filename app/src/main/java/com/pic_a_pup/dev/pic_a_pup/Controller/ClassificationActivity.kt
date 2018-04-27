package com.pic_a_pup.dev.pic_a_pup.Controller

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.github.anastr.speedviewlib.ProgressiveGauge
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pic_a_pup.dev.pic_a_pup.Model.FeedDogSearchResult
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_classification.*
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ClassificationActivity : AppCompatActivity() {

    private var imageFileName: String? = null
    private var galleryUri: Uri? = null
    private var imageFile: File? = null
    private var imageBitmap: Bitmap? = null
    private var latitude: Double? = null
    private var longtiude: Double? = null
    private var mUtility = Utility(this)
    private var postalCode: String? = null
    private var mFirebaseManager = FirebaseManager(this)
    private var imgUrl: String? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private var mImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                10)

        val searchImg = findViewById<ImageView>(R.id.searchImage)
        val locationEditText = findViewById<EditText>(R.id.postalCode_edittext)

        if (intent.getParcelableExtra<Uri>(GALLERY_INTENT_TAG) != null){
            galleryUri = intent.getParcelableExtra(GALLERY_INTENT_TAG)
        }else{
            imageFileName = intent.getStringExtra(IMAGE_INTENT_TAG)
            imageFile = File(imageFileName)
        }

        latitude = intent.getDoubleExtra(LAT_INTENT_TAG, LAT_DEFAULT)
        longtiude = intent.getDoubleExtra(LON_INTENT_TAG, LON_DEFAULT)

        postalCode = mUtility.getZipFromLatLon(latitude.toString(), longtiude.toString())

        setUpNavBar()

        locationEditText.setText(postalCode)

        if(galleryUri != null){
            try{
                imageBitmap = getBitmap(contentResolver,galleryUri)
            }catch (e: FileNotFoundException){
                e.printStackTrace()
            }
        }else{
            imageBitmap = BitmapFactory.decodeFile(imageFile!!.absolutePath)
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(imageFile!!.absolutePath)
            } catch (e:IOException) {
                e.printStackTrace()
            }
        }
        searchImg.setImageBitmap(imageBitmap)

        submit_btn.setOnClickListener(this::onSubmit)
    }

    private fun setUpNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_classification_page)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(2)
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
                        R.id.navigation_collar -> {
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

        navigation_classification_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun onSubmit(view: View) {
        postImageToFirebase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CLASSIFICATION_RESULT && resultCode == Activity.RESULT_OK){
            val stringBreedInfo = intent.getStringExtra("breed_info")
            mFirebaseManager.showToast(stringBreedInfo)
            breed_info_text.text = stringBreedInfo

        }

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

    override fun onStop() {
        super.onStop()
    }

    private fun postImageToFirebase(){
        val restClient = NetworkManager.PaPRestClient.create()
        val fbFile = mFirebaseManager.mStorageReference.child(IMAGE_STORAGE).child(imageBitmap.toString())
        val byteArrayOutputStream = ByteArrayOutputStream()

        imageBitmap!!.compress((Bitmap.CompressFormat.JPEG),50,byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        fbFile.putBytes(data).addOnSuccessListener(this, { taskSnapshot ->
                imgUrl = taskSnapshot.downloadUrl.toString()

        }).addOnCompleteListener{ task ->
            Toast.makeText(this,"Recognizing breed...", Toast.LENGTH_LONG).show()
            restClient.postSearchRequestToServer(postalCode,imgUrl.toString()).
                    enqueue(object: retrofit2.Callback<Model.DogSearchResult> {
                        override fun onFailure(call: Call<Model.DogSearchResult>?, t: Throwable?) {
                            Log.e("Network Call", "Failure ${t.toString()}")
                            updateUiOnResponse("Error","Server Not Responding", null, null, null, null, null)
                        }

                        override fun onResponse(call: Call<Model.DogSearchResult>?, response: Response<Model.DogSearchResult>?) {
                            if (response!!.isSuccessful) {
                                var probability:Float? = null
                                var phone: String? = null
                                var city: String? = null
                                var state: String? = null
                                var zip: String? = null

                                if (response.body()!!.prob != null){
                                    probability = response.body()!!.prob!!.toFloat()
                                }

                                if (response.body()!!.shelter_contact != null) {
                                    phone = response.body()!!.shelter_contact!!.phone
                                    city = response.body()!!.shelter_contact!!.city
                                    state = response.body()!!.shelter_contact!!.state
                                    zip = response.body()!!.shelter_contact!!.zip
                                }

                                if (response.body()?.model_error != null){
                                    updateUiOnResponse("Not Found", null, probability, city, state, zip, phone)

                                } else {
                                    var breedString = response.body()?.breed

                                    if (breedString != null){
                                        val breedInfoString = response.body()!!.breed_info
                                        updateUiOnResponse(breedString, breedInfoString, probability, city, state, zip, phone)
                                        addSearchToTable(breedString,imgUrl!!,probability!!)

                                    } else {
                                        Toast.makeText(this@ClassificationActivity, "Please Retry...",Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } else {
                                when (response.code()){
                                    500 -> {mFirebaseManager.showToast("Server Error")}
                                    502 -> {mFirebaseManager.showToast("Bad Gateway")
                                    }
                                    else -> {mFirebaseManager.showToast("Unknown Error")}
                                }
                            }
                        }
                    })
        }
    }

    fun updateUiOnResponse(breed: String?, breedInfo: String?, probability: Float?,
                           city: String?, state: String?, zip: String?, phone: String?){
        breed_text.text = breed

        if (probability !=null){
            var dogProbBar = findViewById<ProgressiveGauge>(R.id.dog_prob_gauge_classification)
            dogProbBar.speedometerColor = getColor(R.color.colorPrimary)
            dogProbBar.isWithTremble = false
            dogProbBar.setSpeedAt(probability.times(100))
        }

        if (breedInfo != null){
            breed_info_text.text = breedInfo
        } else {
            breed_info_text.text = getString(R.string.cant_identify_breed_message)
        }

        if (city != null) {
            textview_shelter_city.text = city
        } else {
            textview_shelter_city.text = getString(R.string.city_unavailable)
        }

        if (state != null) {
            textview_shelter_state.text = state
        } else {
            textview_shelter_state.text = getString(R.string.state_unavailable)
        }

        if (zip != null) {
            textview_shelter_zip.text = zip
        } else {
            textview_shelter_zip.text = getString(R.string.zip_unavailable)
        }

        if (phone != null) {
            textview_shelter_phone.autoLinkMask = Linkify.PHONE_NUMBERS
            textview_shelter_phone.text = phone
        } else {
            textview_shelter_phone.text = getString(R.string.phone_number_unavailable)
        }

        pre_response_frame.visibility = View.INVISIBLE
        post_response_frame.visibility = View.VISIBLE
        scrollview.visibility = View.VISIBLE
    }

    fun addSearchToTable(breed:String, imageUrl: String, probability: Float){
        var dogSearch = FeedDogSearchResult(breed,imageUrl,probability)
        val keyString = mFirebaseManager.mResultDBRef.push().key
        mFirebaseManager.mResultDBRef.child(keyString).setValue(dogSearch)
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
                ActivityCompat.requestPermissions(this@ClassificationActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
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
                ActivityCompat.requestPermissions(this@ClassificationActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
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
}





