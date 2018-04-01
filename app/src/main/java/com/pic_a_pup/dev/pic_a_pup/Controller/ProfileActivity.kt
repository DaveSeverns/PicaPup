package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import com.pic_a_pup.dev.pic_a_pup.BottomNavigationViewHelper
import com.pic_a_pup.dev.pic_a_pup.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val textView = findViewById<TextView>(R.id.testText)
        val homeFeedActivity = HomeFeedActivity()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_profile_page)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        val menu = bottomNavigationView.menu
        val menuItem = menu.getItem(3)
        menuItem.isChecked = true

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        val intentHome = HomeFeedActivity.newIntent(this)
                        startActivity(intentHome)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_camera -> {
//                        homeFeedActivity.onLaunchCamera()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_collar ->{
                        val intentCollar = CollarActivity.newIntent(this)
                        startActivity(intentCollar)
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

        navigation_profile_page.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
