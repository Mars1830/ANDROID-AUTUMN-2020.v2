package com.example.tabatatimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.tabatatimer.databinding.ActivityTimerPageBinding
import kotlinx.android.synthetic.main.content_timer_page.*
import kotlinx.android.synthetic.main.fragment_edit_timer.view.*

class TimerPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerPageBinding
    private var thisSequence: Sequence? = null
    private lateinit var sequenceHandler : SequenceHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer_page)
        setSupportActionBar(findViewById(R.id.toolbar2))

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        thisSequence = intent.getSerializableExtra("Sequence") as Sequence?
        sequenceHandler = SequenceHandler(thisSequence!!)

        //findViewById<Button>(R.id.playBtn).setOnClickListener { view -> playBtnClicked(view) }
        binding.contentTimerPage.playBtn.setOnClickListener { view -> playBtnClicked(view) }
        binding.contentTimerPage.backBtn.setOnClickListener { view -> backBtnClicked(view) }
        binding.contentTimerPage.forwardBtn.setOnClickListener { view -> forwardBtnClicked(view) }
        binding.contentTimerPage.name.text = thisSequence?.title
        binding.contentTimerPage.name.setTextColor(getColor(ColorUtil.getColorById(thisSequence?.color!!)))
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    private fun playBtnClicked(view: View) {
        if ((view as Button).text == "Play") {
            view.text = "Pause"

            startForegroundService(
                Intent(this, TimerService::class.java).apply {
                    putExtra("Sequence", sequenceHandler)
                    putExtra("Command", "Start")
                })
        }
        else {
            view.text = "Play"

            startForegroundService(
                Intent(this, TimerService::class.java).apply {
                    putExtra("Sequence", sequenceHandler)
                    putExtra("Command", "Pause")
                })
        }
    }

    private fun backBtnClicked(view: View) {
        startForegroundService(
            Intent(this, TimerService::class.java).apply {
                putExtra("Sequence", sequenceHandler)
                putExtra("Command", "BackPhase")
            })
    }

    private fun forwardBtnClicked(view: View) {
        startForegroundService(
            Intent(this, TimerService::class.java).apply {
                putExtra("Sequence", sequenceHandler)
                putExtra("Command", "ForwardPhase")
            })
    }

    override fun onDestroy() {
        stopService(Intent(this, TimerService::class.java))
        super.onDestroy()
    }
}