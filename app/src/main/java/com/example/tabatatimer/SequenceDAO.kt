package com.example.tabatatimer

import androidx.lifecycle.LiveData
import androidx.room.*

import com.example.tabatatimer.Sequence

@Dao
interface SequenceDAO {
    @Query("SELECT * FROM sequence")
    suspend fun getAll(): List<Sequence>

    @get:Query("SELECT * FROM sequence")
    val allSequences: LiveData<List<Sequence>>

    @Query("SELECT * FROM sequence WHERE id LIKE :id")
    suspend fun findById(id: Int): Sequence

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg sequences: Sequence)

    @Update
    suspend fun updateSequences(vararg sequences: Sequence)

    @Delete
    suspend fun delete(sequence: Sequence)

    @Query("DELETE FROM sequence")
    suspend fun deleteAll()
}