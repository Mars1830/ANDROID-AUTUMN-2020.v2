package com.example.tabatatimer

class ColorUtil {

    companion object {
        private val colors : Array<Int> = arrayOf(R.color.s_red, R.color.s_blue, R.color.s_yellow, R.color.s_green)

        fun getColorById(id : Int) : Int{
            return colors[id]
        }
    }
}