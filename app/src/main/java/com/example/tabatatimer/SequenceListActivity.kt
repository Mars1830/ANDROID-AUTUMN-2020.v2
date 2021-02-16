package com.example.tabatatimer

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SequenceListActivity : AppCompatActivity() {

    private lateinit var viewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.MODE_NIGHT_YES
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequence_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view -> onAddClicked(view) }
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.sequenceList.observe(this,
            Observer { sequences: List<Sequence> ->
                loadTable(sequences)
            }
        )
    }

    fun onAddClicked(view: View) {
        val intent = Intent(this, EditTimerActivity::class.java)
        startActivity(intent)
    }

    fun onEditClicked(view: View) {
        val intent = Intent(this, EditTimerActivity::class.java)
        intent.putExtra("Sequence", view.tag as Sequence?)
        startActivity(intent)
    }

    fun onDeleteClicked(view: View) {
        if (view.tag != null) {
            GlobalScope.launch(Dispatchers.IO) {
                viewModel.deleteSequence(view.tag as Sequence)
            }
        }
    }

    fun onNameClicked(view: View) {
        val intent = Intent(this, TimerPageActivity::class.java)
        intent.putExtra("Sequence", view.tag as Sequence?)
        startActivity(intent)
    }

    fun loadTable(sequences: List<Sequence>) {
        findViewById<TableLayout>(R.id.sequenceList).removeAllViews()
        for (i in sequences) {
            createRow(i)
        }
    }

    fun createRow(sequence: Sequence) {
        val row = android.view.LayoutInflater.from(this@SequenceListActivity).inflate(R.layout.sequence_list_row, null) as TableRow

        val name = row.findViewById<TextView>(R.id.sequenceName)
        name.tag = sequence
        name.text = sequence.title
        name.setOnClickListener { view -> onNameClicked(view) }

        val color = ColorUtil.getColorById(sequence.color)
        row.findViewById<TextView>(R.id.sequenceName).setTextColor(getColor(color))
        findViewById<TableLayout>(R.id.sequenceList).addView(row)

        val btnEdit = row.findViewById<Button>(R.id.btnEdit)
        btnEdit.tag = sequence
        btnEdit.setOnClickListener { view -> onEditClicked(view) }

        val btnDelete = row.findViewById<Button>(R.id.button3)
        btnDelete.tag = sequence
        btnDelete.setOnClickListener { view -> onDeleteClicked(view) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
}