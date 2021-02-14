package com.example.tabatatimer

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(Sequence::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sequenceDao(): SequenceDAO
    companion object {

        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "MainDatabase"

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                            //.allowMainThreadQueries() // Uncomment if you don't want to use RxJava or coroutines just yet (blocks UI thread)
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    Log.d("Database", "populating with data...")
                                    GlobalScope.launch(Dispatchers.IO) { rePopulateDb(INSTANCE!!) }
                                }
                            }).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}

fun rePopulateDb(db: AppDatabase) {

}


