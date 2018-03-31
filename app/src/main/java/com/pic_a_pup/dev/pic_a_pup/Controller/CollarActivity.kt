package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import com.pic_a_pup.dev.pic_a_pup.R
import kotlinx.android.synthetic.main.activity_collar.*

class CollarActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = HomeFeedActivity.newIntent(this)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_camera -> {
//                    homeFeedActivity.onLaunchCamera()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_collar ->{
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    val intent = ProfileActivity.newIntent(this)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collar)
        scan_button.setOnClickListener(this::scanBarcode)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CollarActivity::class.java)
        }
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
