package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pic_a_pup.dev.pic_a_pup.R
import kotlinx.android.synthetic.main.activity_collar.*

class CollarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collar)
        scan_button.setOnClickListener(this::scanBarcode)
    }

    fun scanBarcode(view: View){
        val scanBarCodeActivity = Intent(this, CollarScanActivity::class.java)
        startActivityForResult(scanBarCodeActivity,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==0){
            if (data != null){
                val barcode = data.getParcelableArrayExtra("barcode")
                bar_code_result.text = "Barcode Result: ${barcode}"
            }
        }else{

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
