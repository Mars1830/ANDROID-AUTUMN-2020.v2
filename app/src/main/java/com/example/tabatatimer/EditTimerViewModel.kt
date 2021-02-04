package com.example.tabatatimer

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.annotation.AnyThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.room.Room
import java.lang.Exception
import kotlin.concurrent.thread

class EditTimerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        getApplication() as Context,
        AppDatabase::class.java, "MainDatabase"
    ).build()

    private val sequenceDao = db.sequenceDao()

    fun createSequence(newSequence: Sequence) {
        try {
            sequenceDao.insertAll(newSequence)
        }
        catch (e: Exception) {
            val c = e.cause
        }
    }

    fun getAllSequences() : List<Sequence> {
        return sequenceDao.getAll()
    }

    fun updateSequence(oldSequence : Sequence, newSequence: Sequence) {
        newSequence.id = oldSequence.id
        sequenceDao.updateSequences(newSequence)
    }

    fun deleteSequence(oldSequence : Sequence) {
        sequenceDao.delete(oldSequence)
    }

    fun deleteAllSequences() {
        sequenceDao.deleteAll()
    }
}