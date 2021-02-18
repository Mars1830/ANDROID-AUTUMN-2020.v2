package com.example.game

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class StatsActivity : AppCompatActivity() {
    private val user = FirebaseAuth.getInstance().currentUser
    private val users = HashMap<String, UserProfile>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        loadData()
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

    private fun getDate(o: HashMap<String, Int>): LocalDate {
        return LocalDate.of(o["year"]!!  ,o["month"]!!, o["day"]!!)
    }
    private fun getUserName(uid: String, setName: (u:String)->Unit) {
        if (!users.containsKey(uid)) {
            val u = UserProfile()
            u.ReadData(uid, {up->
               users.set(uid, up)
               setName(up.Name)
            })
        }
        else
            users[uid]?.let { setName(it.Name) };
    }
    private fun processItem(o: HashMap<String, *>): GameRecord? {
        val v = o["info"]
        if (v != null) {
            try {
                val info: HashMap<String, String> = v as HashMap<String, String>
                val p1 = info["Player1"]
                val p2 = info["Player2"]
                val sdate: HashMap<String, Int> = info["Date"] as HashMap<String, Int>
                val date = getDate((sdate))
                val color = info["color"]
                var res = info["result"]
                var p1Win: Boolean = res == p1
                var draw: Boolean = res.equals("draw", true)
                val u = user!!.uid!!
                var r: GameRecord? = null
                // if current user was a joiner - we should reverse report data (winner - becomes a looser, white color becomes black color)
                if (u == p1 && res != null) {// first user
                    var s: GameStatus
                    if (draw)
                        s = GameStatus.Draw
                    else if (p1Win)
                        s = GameStatus.Player1Win
                    else
                        s = GameStatus.Player2Win
                    r = GameRecord(date, p1!!, p2!!, color.equals("white", true), s)
                } else if (u == p2 && res != null) {
                    var s: GameStatus
                    if (draw)
                        s = GameStatus.Draw
                    else if (p1Win)
                        s = GameStatus.Player2Win
                    else
                        s = GameStatus.Player1Win
                    r = GameRecord(date, p2!!, p1!!, !color.equals("white", true), s)

                }
                return r
            }
            catch(e: Exception) {}
        }
        return null
    }

    private fun loadData () {
        var dbRef = FirebaseDatabase.getInstance().getReference("Games")
        val stats = ArrayList<GameRecord>()
        dbRef.orderByChild("info").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && p0.value != null) {
                    if (p0.value is ArrayList<*>) {
                        for (i in (p0.value as ArrayList<*>)) {
                            if (i != null) {
                                var g = processItem(i as HashMap<String, *>)
                                if (g != null)
                                    stats.add(g!!)
                            }
                        }
                    } else if (p0.value is HashMap<*, *>) {
                        for (i in (p0.value as HashMap<*, *>)) {
                            if (i != null) {
                                var g = processItem(i.value as HashMap<String, *>)
                                if (g != null)
                                    stats.add(g!!)
                            }
                        }
                    }
                    if (stats.size > 0)
                        buildReport(stats)
                }
            }
        })
    }
    private fun buildReport (report:  ArrayList<GameRecord>) {
        var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val tableLayout = findViewById<TableLayout>(R.id.tbl_report)
        val states = arrayOf(intArrayOf(android.R.attr.state_enabled))
        var colors = intArrayOf(
                Color.RED
        )
        for (rec in report) {
            val row = LayoutInflater.from(this@StatsActivity).inflate(R.layout.stats_tablerow, null) as TableRow
            val c = if (rec.white1) R.string.fig_color_w else R.string.fig_color_b
            row.findViewById<TextView>(R.id.attr_color).setText(getString(c))
            var formattedDate = rec.date.format(formatter)
            row.findViewById<TextView>(R.id.attr_Date).setText(formattedDate)
            getUserName(rec.Player2, { name ->
                row.findViewById<TextView>(R.id.attr_player).setText(name)
            })
            var res =  (row.findViewById<View>(R.id.attr_result) as TextView)
            when(rec.Result) {
                GameStatus.Player1Win -> {
                    colors = intArrayOf(Color.GREEN)
                    res.text = getString(R.string.stats_winner)
                }
                GameStatus.Player2Win -> {
                    colors = intArrayOf(Color.RED)
                    res.text = getString(R.string.stats_loser)
                }
                GameStatus.Draw -> {
                    colors= intArrayOf(Color.BLUE)
                    res.text = getString(R.string.stats_draw)
                }
                else -> {
                    colors = intArrayOf(Color.DKGRAY)
                    res.text = getString(R.string.stats_incompleted)
                }
            }
            res.setTextColor(ColorStateList(states, colors))
            tableLayout.addView(row)
        }
    }
}