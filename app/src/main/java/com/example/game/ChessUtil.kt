package com.example.game

object ChessUtil {

    fun convertLetterToPosition(letter: Char) =
            listOf('-', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').indexOf(letter)

    fun convertPositionToLetter(position: Int) =
            listOf('-', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')[position]

    fun convertPositionToTag(x: Int, y: Int): String = "${convertPositionToLetter(x)}$y"

    fun getXPositionFromTag(tag: String): Int = convertLetterToPosition(tag[0])

    fun getYPositionFromTag(tag: String): Int = Integer.valueOf(tag[1].toString())

    fun sameColor(act1: Actor?, act2: Actor?) : Boolean {
        if (act1 != null && act2 != null) {
          val  s1: String = act1.toString()
          val  s2:String = act2.toString()
          return s1[0] == s2[0];
        }
        else
            return false;
    }
    fun IsWhiteColor(actor: Actor): Boolean {
        return actor.toString()[0] == 'W'
    }

}