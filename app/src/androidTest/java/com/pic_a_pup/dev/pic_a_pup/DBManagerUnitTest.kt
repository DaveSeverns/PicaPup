package com.pic_a_pup.dev.pic_a_pup

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.pic_a_pup.dev.pic_a_pup.DB.DbManager
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DBManagerUnitTest {

    private lateinit var dbManager: DbManager
    private val context = InstrumentationRegistry.getTargetContext()
    private lateinit var testDog: Model.Dog

    @Before
    fun setup(){
        dbManager = DbManager(context)
        testDog = Model.Dog("Spot", "Pug", "8675309", false)
    }

    @Test
    fun shouldAddDogObjectFromDbandGetObjectBackAssertNotNull(){
        dbManager.addDogToDb(testDog)
        var list = dbManager.getDoggosFromDb()
        Log.e("List: ", list.toString())

        Assert.assertEquals("Spot",list.get(3).dogName)
    }
}