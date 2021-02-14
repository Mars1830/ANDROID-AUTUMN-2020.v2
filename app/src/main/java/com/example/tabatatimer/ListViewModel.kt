package com.example.tabatatimer

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.lang.Exception

class ListViewModel(application: Application) : AndroidViewModel(application) {
    /*private val db = Room.databaseBuilder(
        getApplication() as Context,
        AppDatabase::class.java, "MainDatabase"
    ).build()

    private val sequenceDao = db.sequenceDao()*/

    private val sequenceDao: SequenceDAO = AppDatabase.getDatabase(application).sequenceDao()
    val sequenceList: LiveData<List<Sequence>>

    init {
        sequenceList = sequenceDao.allSequences
    }

    suspend fun createSequence(newSequence: Sequence) {
        try {
            sequenceDao.insertAll(newSequence)
        }
        catch (e: Exception) {
            val c = e.cause
        }
    }

    suspend fun getAllSequences() : List<Sequence> {
        return sequenceDao.getAll()
    }

    suspend fun updateSequence(oldSequence : Sequence, newSequence: Sequence) {
        newSequence.id = oldSequence.id
        sequenceDao.updateSequences(newSequence)
    }

    suspend fun deleteSequence(oldSequence : Sequence) {
        sequenceDao.delete(oldSequence)
    }

    suspend fun deleteAllSequences() {
        sequenceDao.deleteAll()
    }
}