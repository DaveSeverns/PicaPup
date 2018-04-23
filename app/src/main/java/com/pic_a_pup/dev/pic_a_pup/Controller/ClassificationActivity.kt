package com.pic_a_pup.dev.pic_a_pup.Controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.github.anastr.speedviewlib.ProgressiveGauge
import com.github.anastr.speedviewlib.SpeedView
import com.pic_a_pup.dev.pic_a_pup.Model.FeedDogSearchResult
import com.pic_a_pup.dev.pic_a_pup.Utilities.BottomNavigationViewHelper
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_classification.*
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.math.roundToInt

class ClassificationActivity : AppCompatActivity() {

    private var imageFileName: String? = null
    private var galleryUri: Uri? = null
    private var imageFile: File? = null
    private var imageBitmap: Bitmap? = null
    private var latitude: Double? = null
    private var longtiude: Double? = null
    private var mUtility = Utility(this)
    private var postalCode: String? = null
    private var searchRequest: Model.ModelSearchRequest? = null
    private var mFirebaseManager = FirebaseManager(this)
    private var imgUrl: String? = null
    private var mDisposable: Disposable? = null
    private var homeFeedActivity = HomeFeedActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)

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
    }

    fun onSubmit(view: View) {
        postImageToFirebase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CLASSIFICATION_RESULT && resultCode == Activity.RESULT_OK){
            val stringBreedInfo = intent.getStringExtra("breed_info")
            mFirebaseManager.showToast(stringBreedInfo)
            breed_info_text.text = stringBreedInfo

        }
    }

    override fun onStop() {
        super.onStop()
        Log.e("Stopped", "OnStop Ran")
    }

    fun postImageToFirebase(){
        val restClient = NetworkManager.PaPRestClient.create()
        val fbFile = mFirebaseManager.mStorageReference.child(IMAGE_STORAGE).child(imageBitmap.toString())
        val byteArrayOutputStream = ByteArrayOutputStream()

        imageBitmap!!.compress((Bitmap.CompressFormat.JPEG),50,byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        Log.e("I actually run", "postImageToFb")
        //mFirebaseManager.showToast("You hit me!!!!!!!!")

        fbFile.putBytes(data).addOnSuccessListener(this, { taskSnapshot ->
                imgUrl = taskSnapshot.downloadUrl.toString()
                //mFirebaseManager.showToast(imgUrl.toString())
                Log.e("Fb Mngr", "Success")
                //searchRequest = Model.ModelSearchRequest(imgUrl.toString(),petfinder_checkbox.isChecked, wiki_check_box.isChecked,postalCode!!)
                //Log.e("Search Request", searchRequest.toString())
                Log.e("Url", imgUrl.toString())


                //Observable.just("").subscribeOn(Schedulers.io()).flatMap { restClient.postSearchRequestToServer(postalCode, imgUrl) }
                //        .map { dsr ->
                //            Model.DogSearchResult(dsr.breed, dsr.breed_info,null, null, null, null) }
                //        .observeOn(AndroidSchedulers.mainThread()).subscribe{res -> print(res.toString())}
                //mFirebaseManager.showToast("Submission Sent")




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

                                println("*****RESPONSE " + response.body())

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
                                        Log.e("Probability $breedString ", probability.toString() )
                                        updateUiOnResponse(breedString, breedInfoString, probability, city, state, zip, phone)
                                        Log.e("Response",breedString )
                                        addSearchToTable(breedString,imgUrl!!,probability!!)

                                    } else {
                                        Toast.makeText(this@ClassificationActivity, "Please Retry...",Toast.LENGTH_SHORT).show()
                                        Log.e("Connection: ", "made but not getting DSR")
                                        Log.e("Probability $breedString ", probability.toString() )

                                    }
                                }

                            } else {
                                when (response.code()){
                                    500 -> {mFirebaseManager.showToast("Server Error")}
                                    502 -> {mFirebaseManager.showToast("Bad Gateway")
                                    Log.e("Error Bod",response.body().toString())}
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
            textview_shelter_city.text = "City unavailable"
        }

        if (state != null) {
            textview_shelter_state.text = state
        } else {
            textview_shelter_state.text = "State unavailable"
        }

        if (zip != null) {
            textview_shelter_zip.text = zip
        } else {
            textview_shelter_zip.text = "Zip unavailable"
        }

        if (phone != null) {
            textview_shelter_phone.text = phone
        } else {
            textview_shelter_phone.text = "Phone number unavailable"
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
}





