package com.pic_a_pup.dev.pic_a_pup.DB

import android.content.Context
import com.pic_a_pup.dev.pic_a_pup.Model.Model
import org.jetbrains.anko.db.*

class DbManager(private val ctx: Context){

    private val dogsDb = DogDbHelper.getInstance(ctx)

    fun addDogToDb(dog: Model.Dog){
        dogsDb.use {
            createTable(DogsSqliteContract.TABLE_NAME,true,
                    DogsSqliteContract.COLUMN_DOG_NAME to TEXT,
                    DogsSqliteContract.COLUMN_DOG_BREED to TEXT,
                    DogsSqliteContract.COLUMN_PUP_CODE to TEXT,
                    DogsSqliteContract.COLUMN_IS_LOST to INTEGER)

            insert(DogsSqliteContract.TABLE_NAME,
                    DogsSqliteContract.COLUMN_DOG_NAME to dog.dogName,
                    DogsSqliteContract.COLUMN_DOG_BREED to dog.dogBreed,
                    DogsSqliteContract.COLUMN_PUP_CODE to dog.pupCode,
                    DogsSqliteContract.COLUMN_IS_LOST to dog.isLost)
        }
    }

    fun getDoggosFromDb(): ArrayList<Model.Dog>{
        val rowParser = classParser<Model.Dog>()
        //needs to explicitly be List so the rowParser return is correct
        var dogList : List<Model.Dog> = ArrayList()
        var listToReturn: ArrayList<Model.Dog> = ArrayList()

        dogsDb.use{
            dogList = select(DogsSqliteContract.TABLE_NAME)
                    .parseList(rowParser)
        }

        for (i in 0 until dogList.size){
            listToReturn.add(Model.Dog(dogList[i].dogName,
                    dogList[i].dogBreed,
                    dogList[i].pupCode,
                    dogList[i].isLost))
        }

        return  listToReturn
    }


}