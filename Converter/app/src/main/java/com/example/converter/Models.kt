package com.example.converter

import kotlin.math.pow
import kotlin.math.round

fun round(x : Double, digits: Int) : Double {
    val pow = 10.0.pow(digits)
    return round(x * pow) / pow
}

object Distance {

    private const val mmBase = 1.0
    private const val mBase = 1000.0
    private const val kmBase = 1000000.0

    fun mm(num : Double, pow: Int) : Double {
        return round(num * mmBase.pow(pow), 7)
    }

    fun m(num : Double, pow: Int) : Double {
        return round(num * mBase.pow(pow), 7)
    }

    fun km(num : Double, pow: Int) : Double {
        return round(num * kmBase.pow(pow), 7)
    }
}

object Weight {

    private const val gBase = 1.0
    private const val kgBase = 1000.0
    private const val poundBase = 453.592

    fun g(num : Double, pow: Int) : Double {
        return round(num * gBase.pow(pow), 7)
    }

    fun kg(num : Double, pow: Int) : Double {
        return round(num * kgBase.pow(pow), 7)
    }

    fun pound(num : Double, pow: Int) : Double {
        return round(num * poundBase.pow(pow), 7)
    }
}

object Temperature {

    fun fromK(num : Double, pow: Int = 0) : Double {
        return round(num, 7)
    }

    fun fromF(num : Double, pow: Int = 0) : Double {
        return round((num + 459.67) * 5 / 9, 7)
    }

    fun fromC(num : Double, pow: Int = 0) : Double {
        return round(num  + 273.15, 7)
    }

    fun toK(num : Double, pow: Int = 0) : Double {
        return round(num, 7)
    }

    fun toF(num : Double, pow: Int = 0) : Double {
        return round(num * 9 / 5 - 459.67, 7)
    }

    fun toC(num : Double, pow: Int = 0) : Double {
        return round(num  - 273.15, 7)
    }
}