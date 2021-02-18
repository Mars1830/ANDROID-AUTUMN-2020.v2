package com.example.game

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.get
import java.lang.StrictMath.abs

class StepValidator {
    private val currentPosition: String
    private val nextPosition: String
    private val currentActor: Actor
    private val figuresContainer: ViewGroup
    private var nextActor: Actor? = null

    constructor (views: ViewGroup, currentPos: String, nextPos: String, act:Actor, nAct: Actor?) {
        currentPosition = currentPos
        nextPosition = nextPos
        currentActor = act
        nextActor = nAct
        figuresContainer = views
    }

    private fun figureExists(pattern: String) : Boolean {
        for(i in 0..figuresContainer.childCount - 1) {
            val view = figuresContainer[i]
            if (view is ChessmanView) {
                if (pattern.contains(view.tag as String, false))
                    return true
            }
        }
        return false
    }
    private fun checkIfSomeOneVertical(): Boolean {
        var res: Boolean = false
        val posYn = ChessUtil.getYPositionFromTag(nextPosition)
        val posYc = ChessUtil.getYPositionFromTag(currentPosition)
        val delta = posYn - posYc
        if (abs(delta) >= 2) { // if less - no space for a figure
            var start = 0
            var end = 0
            if (posYn > posYc) {
                start = posYc + 1
                end = posYn - 1
            }
            else {
                start = posYn + 1
                end = posYc - 1
            }
            var posX = ChessUtil.getXPositionFromTag(currentPosition)
            var pattern = ""
            for (i in start..end) {
                pattern += "-" + ChessUtil.convertPositionToTag(posX, i)
            }
            return figureExists(pattern)
        }
        return res
    }
    private fun checkIfSomeOneHorizontal(): Boolean {
        var res: Boolean = false
        val posXn = ChessUtil.getXPositionFromTag(nextPosition)
        val posXc = ChessUtil.getXPositionFromTag(currentPosition)
        val delta = posXn - posXc
        if (abs(delta) >= 2) { // if less - no space for a figure
            var start = 0
            var end = 0
            if (posXn > posXc) {
                start = posXc + 1
                end = posXn - 1
            }
            else {
                start = posXn + 1
                end = posXc - 1
            }
            var posY = ChessUtil.getYPositionFromTag(currentPosition)
            var pattern = ""
            for (i in start..end) {
                pattern += "-" + ChessUtil.convertPositionToTag(i, posY)
            }
            return figureExists(pattern)
        }
        return res
    }
    private fun checkIfSomeOneDiagonal(): Boolean {
        var res: Boolean = false
        val posXn = ChessUtil.getXPositionFromTag(nextPosition)
        val posXc = ChessUtil.getXPositionFromTag(currentPosition)
        val posYn = ChessUtil.getYPositionFromTag(nextPosition)
        val posYc = ChessUtil.getYPositionFromTag(currentPosition)
        val deltaX = posXn - posXc
        val deltaY = posXn - posXc
        // as we have diagonal abs(deltaX) == abs(deltaY)
        if (abs(deltaX) >= 2) { // if less - no space for a figure
            var startX:Int
            var endX : Int
            var posY: Int
            if (posXn > posXc) {
                startX = posXc + 1
                endX = posXn - 1
            }
            else {
                startX = posXn + 1
                endX = posXc - 1
            }
            if (posYn > posYc)
                posY = posYc + 1
            else
                posY = posYn + 1
            var pattern = ""
            for (i in startX..endX) {
                pattern += "-" + ChessUtil.convertPositionToTag(i, posY++)
            }
            return figureExists(pattern)
        }
        return res
    }

    private fun validateTower() : Boolean {
        val posXc =  ChessUtil.getXPositionFromTag(currentPosition)
        val posXn = ChessUtil.getXPositionFromTag(nextPosition)
        val posYc = ChessUtil.getYPositionFromTag(currentPosition)
        val posYn = ChessUtil.getYPositionFromTag(nextPosition)
        if (posXc == posXn || posYc == posYn) {// only vertical or horizontal movement
            if (posXc == posXn)
                return !checkIfSomeOneVertical()
            else
                return !checkIfSomeOneHorizontal()
        }
        return false
    }

    private fun validatePawn(): Boolean  {
        var res:Boolean = false
        if (checkIfSomeOneVertical()) return false // the is a figure on the way
        if (nextActor == null) {// just a move
            if (ChessUtil.getXPositionFromTag(currentPosition) == ChessUtil.getXPositionFromTag(nextPosition)) {
                val pos = ChessUtil.getYPositionFromTag(nextPosition) - ChessUtil.getYPositionFromTag(currentPosition)
                if (abs(pos) == 1)
                    res = true;
                else if (abs(pos) == 2) {
                   if (ChessUtil.getYPositionFromTag(currentPosition) == 7 && pos == -2 && currentActor == Actor.BP)
                       res = true
                    else  if (ChessUtil.getYPositionFromTag(currentPosition) == 2 && pos == 2 && currentActor == Actor.WP)
                        res = true;
                }
            }
        }
        else {
            val posY = ChessUtil.getYPositionFromTag(nextPosition) - ChessUtil.getYPositionFromTag(currentPosition)
            val posX = ChessUtil.getXPositionFromTag(nextPosition) - ChessUtil.getXPositionFromTag(currentPosition)
            if (abs(posY) == 1 && abs(posX) ==1) {
                if (currentActor == Actor.BP &&  posY < 0)
                    res = true
                else if (currentActor == Actor.WP && posY > 0)
                    res = true
            }
        }
        return res
    }

    private fun checkCoords(x: Int, y: Int) : String {
        if (x > 0 && x <= 8  && y > 0 && y <= 8) // within the border
            return ChessUtil.convertPositionToTag(x, y)
        else
            return ""
    }

    private fun validateHorse(): Boolean {
        val posXc = ChessUtil.getXPositionFromTag(currentPosition)
        val posYc = ChessUtil.getYPositionFromTag(currentPosition)
        var pattern = ""
        // total - 8 possible positions - plus additional check if not putside the board
        pattern += "-" + checkCoords(posXc-1, posYc-2)
        pattern += "-" + checkCoords(posXc-2, posYc-1)
        pattern += "-" + checkCoords(posXc-2, posYc+1)
        pattern += "-" + checkCoords(posXc-1, posYc+2)
        pattern += "-" + checkCoords(posXc+1, posYc+2)
        pattern += "-" + checkCoords(posXc+2, posYc+1)
        pattern += "-" + checkCoords(posXc+2, posYc-1)
        pattern += "-" + checkCoords(posXc+1, posYc-2)
        return pattern.contains(nextPosition, false)
    }

    private fun validateElephant() : Boolean {
        val posXc =  ChessUtil.getXPositionFromTag(currentPosition)
        val posXn = ChessUtil.getXPositionFromTag(nextPosition)
        val posYc = ChessUtil.getYPositionFromTag(currentPosition)
        val posYn = ChessUtil.getYPositionFromTag(nextPosition)
        // check if diagonal
        if (abs(posXc - posXn) == abs(posYc - posYn)) {
            return !checkIfSomeOneDiagonal()
        }
        return false
    }

    private fun validateKing() : Boolean {
        val posXc =  ChessUtil.getXPositionFromTag(currentPosition)
        val posXn = ChessUtil.getXPositionFromTag(nextPosition)
        val posYc = ChessUtil.getYPositionFromTag(currentPosition)
        val posYn = ChessUtil.getYPositionFromTag(nextPosition)
        return (abs(posXc - posXn) <= 1 && abs(posYc - posYn) <= 1)
    }

    private fun validateQueen() : Boolean {
        val posXc =  ChessUtil.getXPositionFromTag(currentPosition)
        val posXn = ChessUtil.getXPositionFromTag(nextPosition)
        val posYc = ChessUtil.getYPositionFromTag(currentPosition)
        val posYn = ChessUtil.getYPositionFromTag(nextPosition)
        if (posXc == posXn || posYc == posYn)
            return validateTower()
        else
            return validateElephant()
    }


    fun Validate(wp: Boolean) : Boolean {

        if (!ChessUtil.sameColor(currentActor, nextActor))
        {
            val whiteActor = ChessUtil.IsWhiteColor(currentActor)
            if ((wp && whiteActor) || ((!wp) && (!whiteActor))) {
                if (currentActor == Actor.WP || currentActor == Actor.BP)
                    return validatePawn()
                else if (currentActor == Actor.BR || currentActor == Actor.WR)
                    return validateTower();
                else if (currentActor == Actor.BN || currentActor == Actor.WN)
                    return validateHorse()
                else if (currentActor == Actor.BB || currentActor == Actor.WB)
                    return validateElephant()
                else if (currentActor == Actor.BK || currentActor == Actor.WK)
                    return validateKing()
                else if (currentActor == Actor.BQ || currentActor == Actor.WQ)
                    return validateQueen()
                return false;
            }
            else
                return false
        }
        else
            return false
    }
}