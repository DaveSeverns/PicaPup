package com.pic_a_pup.dev.pic_a_pup.Controller

import android.Manifest
import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.location.Location
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.zxing.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ChildEventListener
import com.pic_a_pup.dev.pic_a_pup.Model.DogLover
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.FirebaseManager
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.jetbrains.anko.alert


class QRCollarActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView
    private val camId = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
    private val REQUEST_CAMERA = 1
    private val mFirebaseManager = FirebaseManager(this)
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private lateinit var qrAlert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        val currentApiVersion = Build.VERSION.SDK_INT

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@QRCollarActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10)
                return
            }

        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                mLocation = location
            }
        }

        sendSMS()
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(applicationContext, "Scan QR code on collar", Toast.LENGTH_LONG).show()
            } else {
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), REQUEST_CAMERA)
    }

    public override fun onResume() {
        super.onResume()

        val currentapiVersion = android.os.Build.VERSION.SDK_INT
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = ZXingScannerView(this)
                    setContentView(scannerView)
                }
                scannerView.setResultHandler(this)
                scannerView.startCamera()
            } else {
                requestPermission()
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA -> if (grantResults.size > 0) {

                val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted) {
                    Toast.makeText(applicationContext, "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(arrayOf(CAMERA),
                                                    REQUEST_CAMERA)
                                        }
                                    })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        android.support.v7.app.AlertDialog.Builder(this@QRCollarActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    override fun handleResult(result: Result?) {
        var dogName: String? = null
        var dogLoverName: String? = null
        var dogLoverNumber: String? = null
        var dogLoverFCM: String? = null
        val myResult = result?.getText()
        Log.d("QRCodeScanner", result?.getText());
        Log.d("QRCodeScanner", result?.getBarcodeFormat().toString());
        try{
            mFirebaseManager.mLostDogDBRef.child(myResult).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot?) {
                    Log.e("Pup", snapshot.toString())
                    if(snapshot!!.value != null){
                        dogName = snapshot.child("dogName").value as String
                        dogLoverName = snapshot.child("dogLover").child("name").value as String
                        dogLoverNumber = snapshot.child("dogLover").child("phoneNumber").value as String
                        dogLoverFCM = snapshot.child("fcm_id").value as String
                        var map = HashMap<String,Any>()
                        map.put("found",true)
                        map.put("latitude", mLocation!!.latitude)
                        map.put("longitude", mLocation!!.longitude)
                        mFirebaseManager.mLostDogDBRef.child(myResult).updateChildren(map)

                        Log.e("Dog Lover ", "$dogLoverName and phone number $dogLoverNumber")
                        Log.e("Dog Name ", dogName)
                        lostDogDialog(dogName,dogLoverName,dogLoverNumber,myResult)
                    }else{
                        noDogFoundDialog(myResult)
                    }
                }

                override fun onCancelled(p0: DatabaseError?) {
                    Log.e("Error: ", "DatabaseError Dog not found")
                    noDogFoundDialog(myResult)
                }
            })
        }catch (e: Exception){
            noDogFoundDialog(myResult)
            e.printStackTrace()
        }

    }

    @SuppressLint("SetTextI18n")
    fun lostDogDialog(dogNameD:String?, ownerNameD:String?, phoneNumberOfOwner:String?, pupCode: String?){
        var textView = TextView(this)
        val formatedString = """$ownerNameD's dog,
        |you can reach them at:
        |$phoneNumberOfOwner""".trimMargin()
        textView.textSize = 16f

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.qr_scan_dialog, null)
        dialogBuilder.setView(dialogView)
        val qrTextView = dialogView.findViewById<TextView>(R.id.text_view_dialog_message_qr)
        qrTextView.text = formatedString
        val okBtn = dialogView.findViewById<Button>(R.id.button_ok)
        //var builder = AlertDialog.Builder(this)
        //builder.setTitle(formatedString).setView(textView)

        okBtn.setOnClickListener(View.OnClickListener {
            try{
                Log.e("Text finna be sent"," fam")
                sendSMS()
                SmsManager.getDefault().sendTextMessage(phoneNumberOfOwner,null,
                        "Found your dog, $dogNameD!",
                        null,
                        null)
            }catch (e: Exception){
                e.printStackTrace()
            }
            scannerView.resumeCameraPreview(this)
            qrAlert.dismiss()
        })
        qrAlert = dialogBuilder.create()
        qrAlert.show()



        /*@SuppressLint("SetTextI18n")
    fun lostDogDialog(dogNameD:String?, ownerNameD:String?, phoneNumberOfOwner:String?, pupCode: String?){
        var textView = TextView(this)
        val formatedString = "You Found $dogNameD!"
        textView.text = """$ownerNameD's dog,
        |you can reach them at:
        |$phoneNumberOfOwner""".trimMargin()
        textView.textSize = 16f

        var builder = AlertDialog.Builder(this)
        builder.setTitle(formatedString).setView(textView)
        builder.setPositiveButton("OK", DialogInterface
                .OnClickListener({ dialogInterface: DialogInterface, i: Int ->

            try{
                Log.e("Text finna be sent"," fam")
                sendSMS(phoneNumberOfOwner,"Dog Found")
                SmsManager.getDefault().sendTextMessage(phoneNumberOfOwner,null,
                        "Found your dog, $dogNameD!",
                        null,
                        null)
            }catch (e: Exception){
                e.printStackTrace()
            }
            scannerView.resumeCameraPreview(this)

        }))

        builder.setMessage(pupCode)
        var alert1 = builder.create();
        alert1.show();

    }*/

    }

    fun noDogFoundDialog(codeFound: String?){
        var builder = AlertDialog.Builder(this)
        builder.setTitle("No Dog reported Lost")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener({dialog: DialogInterface?, which: Int ->
            scannerView.resumeCameraPreview(this)
        }))
        builder.setMessage("With Pup Code: $codeFound")
        builder.show()
    }

    fun sendSMS(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.SEND_SMS)){
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS),10)
            }
        }
    }


}