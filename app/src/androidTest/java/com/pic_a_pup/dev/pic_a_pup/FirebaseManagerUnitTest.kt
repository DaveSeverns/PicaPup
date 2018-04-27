package com.pic_a_pup.dev.pic_a_pup

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pic_a_pup.dev.pic_a_pup.Utilities.FirebaseManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by davidseverns on 3/18/18.
 */
@RunWith(AndroidJUnit4::class)
class FirebaseManagerUnitTest {

    private lateinit var firebaseManager: FirebaseManager
    @Before fun setup(){
        firebaseManager = FirebaseManager(InstrumentationRegistry.getContext())
    }

    @Test fun testLoginMethodShouldBeInvalidLogin(){
        firebaseManager.logUserIntoFirebase("test","test")
        val user = FirebaseAuth.getInstance().currentUser
        Assert.assertNotEquals("test",user!!.email)
    }

    @Test fun testLoginMethodShouldBeValidLogin(){
        firebaseManager.logUserIntoFirebase("dave@pap.com","pass123")
        val user = FirebaseAuth.getInstance().currentUser
        Assert.assertTrue("dave@pap.com".equals(user!!.email))
    }
}