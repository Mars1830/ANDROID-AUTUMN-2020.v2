package com.example.tabatatimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.tabatatimer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
    }

    fun themeBtnClicked(view: View) {
        if ((view as Button).text == "Dark theme") {
            view.text = "Light theme"
            //ColorUtil.systemTheme = R.style.Theme_TabataTimer_Dark

        }
        else {
            view.text = "Dark theme"
            //ColorUtil.systemTheme = R.style.Theme_TabataTimer_Light
        }
    }
}