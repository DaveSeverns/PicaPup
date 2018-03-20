package com.pic_a_pup.dev.pic_a_pup.Controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.IMAGE_INTENT_TAG
import java.io.File
import java.io.IOException

class ClassificationActivity : AppCompatActivity() {

    private var imageFileName : String? = null
    private var imageFile : File? = null
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)
        val searchImg = findViewById<ImageView>(R.id.searchImage)

        imageFileName  = intent.getStringExtra(IMAGE_INTENT_TAG)

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

    }
}
