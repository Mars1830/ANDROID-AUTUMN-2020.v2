package com.example.tabatatimer

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {
    lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("props", MODE_PRIVATE)
        updateFont()
        updateLanguage()
    }

    fun updateFont() {
        val configuration: Configuration = resources.configuration
        configuration.fontScale = prefs.getFloat("font_size", 1f)

        val metrics: DisplayMetrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager?
        wm!!.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density

        resources.updateConfiguration(configuration, metrics)
    }

    fun updateLanguage() {
        val configuration: Configuration = resources.configuration
        val locale = Locale(prefs.getString("language", "en")!!)
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}