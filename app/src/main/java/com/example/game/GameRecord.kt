package com.example.game

import java.time.LocalDate
import java.util.*

data class GameRecord(val date: LocalDate, val Player1: String, val Player2: String, val white1: Boolean, val Result: GameStatus )
