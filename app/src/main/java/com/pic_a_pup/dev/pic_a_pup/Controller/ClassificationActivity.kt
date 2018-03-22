package com.pic_a_pup.dev.pic_a_pup.Controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_classification.*
import java.io.File
import java.io.IOException

class ClassificationActivity : AppCompatActivity() {

    private var imageFileName : String? = null
    private var imageFile : File? = null
    private var imageBitmap: Bitmap? = null
    private var latitude: Double? = null
    private var longtiude: Double? = null
    private var mUtility = Utility(this)
    private var postalCode: String? = null
    private var searchRequest: Model.ModelSearchRequest? = null
    private var mFirebaseManager = FirebaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)
        val searchImg = findViewById<ImageView>(R.id.searchImage)
        val locationEditText = findViewById<EditText>(R.id.postalCode_edittext)

        imageFileName  = intent.getStringExtra(IMAGE_INTENT_TAG)
        latitude = intent.getDoubleExtra(LAT_INTENT_TAG, LAT_DEFAULT)
        longtiude = intent.getDoubleExtra(LON_INTENT_TAG, LON_DEFAULT)

        postalCode = mUtility.getZipFromLatLon(latitude.toString(),longtiude.toString())



        locationEditText.setText(postalCode)

        imageFile = File(imageFileName)


        if (imageFile!!.exists()) {
            imageBitmap = BitmapFactory.decodeFile(imageFile!!.absolutePath)
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(imageFile!!.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }


            searchImg.setImageBitmap(imageBitmap)
        }

        submit_btn.setOnClickListener(this::onSubmit)


    }

    fun onSubmit(view: View){
        var url: String
        if(imageFile!!.exists()){
            url = mFirebaseManager.postImageToFireBaseForUrl(imageFileName!!, imageFile!!)

            searchRequest = Model.ModelSearchRequest(url,petfinder_checkbox.isChecked, wiki_check_box.isChecked,postalCode!!)
            Log.e("Search Request", searchRequest.toString())
        }
    }
}
