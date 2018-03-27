package com.pic_a_pup.dev.pic_a_pup.Controller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.UploadTask
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_classification.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification)
        val searchImg = findViewById<ImageView>(R.id.searchImage)
        val locationEditText = findViewById<EditText>(R.id.postalCode_edittext)

        imageFileName = intent.getStringExtra(IMAGE_INTENT_TAG)
        latitude = intent.getDoubleExtra(LAT_INTENT_TAG, LAT_DEFAULT)
        longtiude = intent.getDoubleExtra(LON_INTENT_TAG, LON_DEFAULT)

        postalCode = mUtility.getZipFromLatLon(latitude.toString(), longtiude.toString())



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

    fun onSubmit(view: View) {
        postImageToFirebase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CLASSIFICATION_RESULT && resultCode == Activity.RESULT_OK){
            mFirebaseManager.showToast(intent.getStringExtra("Url"))

        }
    }

    fun postImageToFirebase(){
        val restClient = NetworkManager.PaPRestClient.create()
        val fbFile = mFirebaseManager.mStorageReference.child(IMAGE_STORAGE).child(imageBitmap.toString())
        val fileUri = Uri.fromFile(imageFile)
        Log.e("I actually run", "postImageToFb")
        //mFirebaseManager.showToast("You hit me!!!!!!!!")


        fbFile.putFile(fileUri).addOnSuccessListener {
            OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                imgUrl = taskSnapshot.downloadUrl.toString()
                //mFirebaseManager.showToast(imgUrl.toString())
                Log.e("Fb Mngr", "Success")
                searchRequest = Model.ModelSearchRequest(imgUrl.toString(),petfinder_checkbox.isChecked, wiki_check_box.isChecked,postalCode!!)
                Log.e("Search Request", searchRequest.toString())
                Log.e("Url", imgUrl.toString())
                //mFirebaseManager.showToast("Submission Sent")
                restClient.postSearchRequestToServer(postalCode,imgUrl.toString()).enqueue(
                        object: retrofit2.Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                                Log.e("Response", response!!.body().toString())
                            }

                        })

                val intent = Intent(this,ClassificationActivity:: class.java)
                intent.putExtra("Url", imgUrl)
                startActivityForResult(intent, CLASSIFICATION_RESULT)
            }
        }
    }



}

