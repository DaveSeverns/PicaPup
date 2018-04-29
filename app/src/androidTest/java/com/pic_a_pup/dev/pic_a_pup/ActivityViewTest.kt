package com.pic_a_pup.dev.pic_a_pup

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import com.pic_a_pup.dev.pic_a_pup.Controller.LoginActivity
import org.junit.Rule
import org.junit.runner.RunWith
import com.beust.klaxon.convert
import org.junit.Before
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class ActivityViewTest{

    @Before
    fun setup(){
    }
    //@get:Rule @JvmField var activity = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    @Test
    fun didViewLoad(){
        Espresso.onView(withId(R.layout.activity_login)).check(matches(isDisplayed()))
    }


}