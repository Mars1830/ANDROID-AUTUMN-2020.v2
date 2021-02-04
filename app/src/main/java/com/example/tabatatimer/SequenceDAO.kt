package com.example.tabatatimer

import androidx.room.*

import com.example.tabatatimer.Sequence

@Dao
interface SequenceDAO {
    @Query("SELECT * FROM sequence")
    fun getAll(): List<Sequence>

    @Query("SELECT * FROM sequence WHERE id LIKE :id")
    fun findById(id: Int): Sequence

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg sequences: Sequence)

    @Update
    fun updateSequences(vararg sequences: Sequence)

    @Delete
    fun delete(sequence: Sequence)

    @Query("DELETE FROM sequence")
    fun deleteAll()
}