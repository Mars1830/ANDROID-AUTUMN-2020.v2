package com.example.tabatatimer

import java.io.Serializable

class SequenceHandler(sequence: Sequence) : Serializable{

    var currentPhase = 0
    val phases : ArrayList<Long>
    val phasesOriginal : ArrayList<Long>

    init {
        val phasesNumber = 2 + sequence.cycles * 2
        phases = ArrayList<Long>(phasesNumber)
        phasesOriginal = ArrayList<Long>(phasesNumber)

        phases.add(sequence.prepare.toLong() * 1000)
        for (i in 0 until sequence.cycles) {
            phases.add(sequence.work.toLong() * 1000)
            phases.add(sequence.rest.toLong() * 1000)
        }
        phases.add(sequence.cooldown.toLong() * 1000)

        for (i in 0 until phasesNumber) {
            phasesOriginal.add(phases[i])
        }
    }

    fun getCountdown() : Long {
        return phases[currentPhase]
    }

    fun setCountDown(millis : Long) {
        phases[currentPhase] = millis
    }

    fun moveToNextPhase() : Boolean {
        if (currentPhase < phases.size - 1) {
            currentPhase++
            phases[currentPhase] = phasesOriginal[currentPhase]
            return true
        }
        return false
    }

    fun moveToPrevPhase() : Boolean {
        if (currentPhase > 0) {
            currentPhase--
            phases[currentPhase] = phasesOriginal[currentPhase]
            return true
        }
        return false
    }

    fun getPhaseName() : String {
        if (currentPhase == 0) {
            return "Prepare"
        }
        else if (currentPhase == phases.size - 1) {
            return "Cooldown"
        }
        else if (currentPhase % 2 == 1) {
            return "Work"
        }
        else {
            return "Rest"
        }
    }
}