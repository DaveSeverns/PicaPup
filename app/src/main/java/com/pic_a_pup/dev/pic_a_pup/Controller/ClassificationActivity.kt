package com.pic_a_pup.dev.pic_a_pup.Controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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
import java.io.IOException
import java.io.Serializable

class ClassificationActivity : AppCompatActivity() {

    private var imageFileName: String? = null
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

        imageFileName = intent.getStringExtra(IMAGE_INTENT_TAG)
        latitude = intent.getDoubleExtra(LAT_INTENT_TAG, LAT_DEFAULT)
        longtiude = intent.getDoubleExtra(LON_INTENT_TAG, LON_DEFAULT)

        postalCode = mUtility.getZipFromLatLon(latitude.toString(), longtiude.toString())

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_classification_page)
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

        navigation_classification_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        locationEditText.setText(postalCode)

        imageFile = File(imageFileName)

        if (imageFile!!.exists()) {
            imageBitmap = BitmapFactory.decodeFile(imageFile!!.absolutePath)
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(imageFile!!.absolutePath)
            } catch (e:IOException) {
                e.printStackTrace()
            }


            searchImg.setImageBitmap(imageBitmap)
        }

        submit_btn.setOnClickListener(this::onSubmit)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ClassificationActivity::class.java)
        }
    }

    fun onSubmit(view: View) {
        postImageToFirebase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CLASSIFICATION_RESULT && resultCode == Activity.RESULT_OK){
            val stringBreedInfo = intent.getStringExtra("breed_info")
            mFirebaseManager.showToast(stringBreedInfo)
            wiki_check_box.text = stringBreedInfo

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
            Toast.makeText(this,"Upload complete, breed info incoming...", Toast.LENGTH_SHORT).show()
            restClient.postSearchRequestToServer(postalCode,imgUrl.toString()).
                    enqueue(object: retrofit2.Callback<Model.DogSearchResult> {
                        override fun onFailure(call: Call<Model.DogSearchResult>?, t: Throwable?) {
                            Log.e("Network Call", "Failure ${t.toString()}")
                            updateUiOnResponse("Error","Server Not Responding")
                        }

                        override fun onResponse(call: Call<Model.DogSearchResult>?, response: Response<Model.DogSearchResult>?) {
                            if(response!!.isSuccessful){
                                var breedString = response.body()?.breed
                                if(breedString != null){
                                    val breedInfoString = response.body()!!.breed_info
                                    updateUiOnResponse(breedString,breedInfoString)
                                    Log.e("Response",breedString )
                                }else{
                                    Log.e("Connection: ", "made but not getting DSR")
                                    breedString = "no data from server"
                                }
                            }else{
                                when (response.code()){
                                    500 -> {mFirebaseManager.showToast("Server Error")}
                                    502 -> {mFirebaseManager.showToast("Bad Gateway")
                                    Log.e("Error Bod",response.body().toString())}
                                    else -> {mFirebaseManager.showToast("Unknown Error")}
                                }
                            }



                              //val intent = Intent(applicationContext,ClassificationActivity:: class.java)
                              //intent.putExtra("breed_info", breedInfoString)
                              //startActivityForResult(intent, CLASSIFICATION_RESULT)
                           }

                    })
        }
    }


    fun updateUiOnResponse(breed: String?, breedInfo: String?){
        breed_text.text = breed
        if (breedInfo != null){
            breed_info_text.text = breedInfo
        }
        pre_response_frame.visibility = View.INVISIBLE
        post_response_frame.visibility = View.VISIBLE
    }
}





