package com.pic_a_pup.dev.pic_a_pup

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.pic_a_pup.dev.pic_a_pup.Model.User
import com.pic_a_pup.dev.pic_a_pup.Utilities.Utility
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by davidseverns on 3/18/18.
 */
@RunWith(AndroidJUnit4::class)
class UtilityUnitTest {
    var mUtility = Utility(InstrumentationRegistry.getContext())

    @Test
    fun isValidEmailShouldReturnTrue(){
        // example of bad email
        var invalidEmail = "fakeat123.com"

        var validEmail = "real@legit.edu"

        /**
         * to confirm the function is working it should recognize validEmail as "true"
         * and invalidEmail as "false"
         */
        assertTrue(mUtility.isValidEmail(validEmail) && !mUtility.isValidEmail(invalidEmail))

    }


    @Test
    fun isValidPasswordShouldReturnTrue(){
        var invalidPass = "easyP"
        var validPass = "Pass@123"

        assertTrue(mUtility.isValidPassword(validPass) && !mUtility.isValidPassword(invalidPass))
    }

    @Test
    fun userAsJSONObject(){
        var testUser = User("123", "Foo",null)

        var testUserJson = mUtility.convertToJSON(testUser)

        assertEquals("123", testUserJson.get("userId"))
        assertEquals("Foo", testUserJson.get("name"))
    }
}