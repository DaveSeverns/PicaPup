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
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.pic_a_pup.dev.pic_a_pup.Model.FeedDogSearchResult
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_home_feed.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HomeFeedActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private var mImagePath: String? = null
    private lateinit var mUtility: Utility
    private lateinit var mFirebaseManager: FirebaseManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FirebaseRecyclerAdapter<FeedDogSearchResult,ResultViewHolder>
    private lateinit var mResDBRefQuery: Query
    private lateinit var viewManager: RecyclerView.LayoutManager
    val dogsSearched = arrayListOf<Model.DogSearchResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_feed)
        recyclerView = findViewById<RecyclerView>(R.id.search_feed_recycler)
        mFirebaseManager = FirebaseManager(this)
        mResDBRefQuery = FirebaseDatabase.getInstance().reference.child(RESULTS_TABLE).limitToLast(10)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //request the necessary permissions
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                10)

        mAuth = FirebaseAuth.getInstance()
        mUtility = Utility(this)

        setUpNavBar()

        val options: FirebaseRecyclerOptions<FeedDogSearchResult> = FirebaseRecyclerOptions.Builder<FeedDogSearchResult>()
                .setQuery(mResDBRefQuery, FeedDogSearchResult::class.java).build()

        viewAdapter = object : FirebaseRecyclerAdapter<FeedDogSearchResult, ResultViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
                val view = LayoutInflater.from(parent!!.context).inflate(R.layout.search_result_card, parent, false)
                return ResultViewHolder(view)            }


            override fun onBindViewHolder(holder: ResultViewHolder, position: Int, model: FeedDogSearchResult) {
                holder.onBindView(this@HomeFeedActivity, model.dogImageSent!!, model.probability, model.breed)
            }
        }

        recyclerView.adapter = viewAdapter

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun setUpNavBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_home_page)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(0)
        menuItem.isChecked = true

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
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

        navigation_home_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, HomeFeedActivity::class.java)
        }
    }

    override fun onStart() {
        super.onStart()
        var currentUser = mAuth.currentUser

        viewAdapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        viewAdapter.notifyDataSetChanged()
    }

    private fun onLaunchCamera() {
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
                ActivityCompat.requestPermissions(this@HomeFeedActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if(id == R.id.log_out_button){
            mAuth.signOut()
            val logOutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logOutIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
    }

    override fun onStop() {
        super.onStop()
        viewAdapter.stopListening()
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
}
