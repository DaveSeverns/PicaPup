package com.pic_a_pup.dev.pic_a_pup.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DogDbHelper(context: Context): ManagedSQLiteOpenHelper(context,DogsSqliteContract.TABLE_NAME) {

    companion object {
        private var instance : DogDbHelper? = null

        @Synchronized
        fun getInstance(context: Context): DogDbHelper{
            if (instance == null){
                instance = DogDbHelper(context.applicationContext)
            }
            return instance!!
        }
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(DogsSqliteContract.TABLE_NAME,true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(DogsSqliteContract.TABLE_NAME,true,
                DogsSqliteContract.COLUMN_DOG_NAME to TEXT,
                DogsSqliteContract.COLUMN_DOG_BREED to TEXT,
                DogsSqliteContract.COLUMN_PUP_CODE to TEXT,
                DogsSqliteContract.COLUMN_IS_LOST to INTEGER)
    }

}

val Context.dogdatabase : DogDbHelper
    get() = DogDbHelper.getInstance(applicationContext)