package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import com.pic_a_pup.dev.pic_a_pup.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var homeFeedActivity = HomeFeedActivity()

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
                    val intent = CollarActivity.newIntent(this)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val textView = findViewById<TextView>(R.id.testText)

        navigation_profile_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
