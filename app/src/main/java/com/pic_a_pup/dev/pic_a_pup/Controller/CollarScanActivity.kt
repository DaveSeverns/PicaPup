package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.SparseArray
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.pic_a_pup.dev.pic_a_pup.Manifest
import com.pic_a_pup.dev.pic_a_pup.R
import kotlinx.android.synthetic.main.activity_collar_scan.*
import java.io.IOException

class CollarScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collar_scan)


        createCameraSource()
    }

    private fun createCameraSource() {
        val decoder = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()

        var cameraSource = CameraSource.Builder(this,decoder).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024).build()

        camera_preview.holder?.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder?) {
                if(ActivityCompat.checkSelfPermission(this@CollarScanActivity,android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    return
                }
                try {
                    cameraSource.start(camera_preview.holder)

                }catch (e: IOException){
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }
        })

        decoder.setProcessor(object : Detector.Processor<Barcode>{
            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val barcodes = p0?.detectedItems

                if (barcodes!!.size() > 0){
                    val intent = Intent()
                    intent.putExtra("barcode", barcodes.valueAt(0))
                    setResult(CommonStatusCodes.SUCCESS, intent)
                    finish()
                }
            }

            override fun release() {
            }
        })
    }
}
