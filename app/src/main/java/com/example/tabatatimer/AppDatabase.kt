package com.example.tabatatimer

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = arrayOf(Sequence::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sequenceDao(): SequenceDAO
}