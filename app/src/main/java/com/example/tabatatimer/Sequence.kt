package com.example.tabatatimer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Sequence(@ColumnInfo(name = "title") var title: String,
                    @ColumnInfo(name = "color") var color: Int,
                    @ColumnInfo(name = "prepare") var prepare: Int,
                    @ColumnInfo(name = "work") var work: Int,
                    @ColumnInfo(name = "rest") var rest: Int,
                    @ColumnInfo(name = "cycles") var cycles: Int,
                    @ColumnInfo(name = "cooldown") var cooldown: Int) : Serializable {


    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = idCounter
    //var currentPhase
    //var currentSecond = 0

    init {
        idCounter += 1
    }

    companion object {
        var idCounter : Int = 1
    }


}