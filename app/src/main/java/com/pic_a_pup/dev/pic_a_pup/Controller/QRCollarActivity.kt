package com.pic_a_pup.dev.pic_a_pup.Controller

import android.Manifest.permission.CAMERA
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Camera
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.zxing.Result
import com.pic_a_pup.dev.pic_a_pup.R
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.os.Build
import android.widget.Toast
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.pic_a_pup.dev.pic_a_pup.Utilities.FirebaseManager


class QRCollarActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView
    private val camId = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
    private val REQUEST_CAMERA = 1
    private val mFirebaseManager = FirebaseManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        val currentApiVersion = Build.VERSION.SDK_INT

        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(applicationContext, "Permission already granted!", Toast.LENGTH_LONG).show()
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
        val myResult = result?.getText();
        Log.d("QRCodeScanner", result?.getText());
        Log.d("QRCodeScanner", result?.getBarcodeFormat().toString());

        var builder = AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", DialogInterface.OnClickListener({ dialogInterface: DialogInterface, i: Int ->

            scannerView.resumeCameraPreview(this)

        }))
        builder.setNeutralButton("Visit", DialogInterface.OnClickListener( { dialogInterface: DialogInterface, i: Int ->

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(myResult))
            startActivity(browserIntent);

        }))

        builder.setMessage(result?.getText())
        var alert1 = builder.create();
        alert1.show();

    }

   //fun getListOfLostDogs(): ArrayList<Any>{
   //    val dogs = mFirebaseManager.mLostDogDBRef
   //}
}
