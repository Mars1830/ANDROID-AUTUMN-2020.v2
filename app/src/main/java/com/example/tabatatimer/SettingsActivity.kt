package com.example.tabatatimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.tabatatimer.databinding.ActivitySettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsActivity : BaseActivity(){

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: ListViewModel
    private var isDarkMode : Boolean = false
    private var fontSize : Float = 1f
    private var language : String = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        isDarkMode = prefs.getBoolean("dark_mode", false)
        fontSize = prefs.getFloat("font_size", 1f)
        language = prefs.getString("language", "en")!!
        if (isDarkMode) {
            binding.themeBtn.setText(R.string.light_theme)
        }
        else {
            binding.themeBtn.setText(R.string.dark_theme)
        }

        ArrayAdapter.createFromResource(
                this,
                R.array.font_size,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.editFontSize.adapter = adapter
        }
        binding.editFontSize.setSelection(if (fontSize == 1f) 0 else 1)

        ArrayAdapter.createFromResource(
                this,
                R.array.language,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.editLanguage.adapter = adapter
        }
        binding.editLanguage.setSelection(if (language == "en") 0 else 1)
    }

    fun themeBtnClicked(view: View) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        isDarkMode = !isDarkMode
        val ed = prefs.edit()
        ed.putBoolean("dark_mode", isDarkMode)
        ed.apply()
    }

    fun clearAllBtnClicked(view: View) {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.deleteAllSequences()
        }
    }

    private fun changeFont() {
        fontSize = when (binding.editFontSize.selectedItem.toString()) {
            getText(R.string.standard) -> 1f
            getText(R.string.large) -> 1.5f
            else -> 1f
        }
        val ed = prefs.edit()
        ed.putFloat("font_size", fontSize)
        ed.apply()
    }


    fun okBtnClicked(view: View) {
        changeFont()
        changeLanguage()

        val intent = Intent(this, SequenceListActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun cancelBtnClicked(view: View) {
        val intent = Intent(this, SequenceListActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changeLanguage() {
        language = when (binding.editLanguage.selectedItem.toString()) {
            getText(R.string.en) -> "en"
            getText(R.string.ru) -> "ru"
            else -> "en"
        }
        val ed = prefs.edit()
        ed.putString("language", language)
        ed.apply()
    }
}