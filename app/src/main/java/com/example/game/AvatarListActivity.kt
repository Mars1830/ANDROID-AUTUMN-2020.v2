package com.example.game

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity


class AvatarListActivity : AppCompatActivity() {
    private var resId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        buildAvatarList()
    }
    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.getItemId()) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }


    private fun buildAvatarList() {
        val tableLayout = findViewById<TableLayout>(R.id.tbl_avatars)
        val cards = intArrayOf(
            R.drawable.a1,
            R.drawable.a2,
            R.drawable.a3,
            R.drawable.a4,
            R.drawable.a5,
            R.drawable.a6,
            R.drawable.a7,
            R.drawable.a8
        )
        for (i in 1..8) {
            val row = LayoutInflater.from(this@AvatarListActivity).inflate(
                R.layout.avatar_item,
                null
            ) as TableRow
            val im = row.findViewById<ImageView>(R.id.img_avataricon)
            im.setImageResource(cards[i - 1])
            im.tag = cards[i - 1]
            im.setOnClickListener({ v -> onImageClick(v) })
            tableLayout.addView(row);
        }
    }

    fun onImageClick(view: View) {
        resId = view.tag as Int
        val _result = Intent()
        _result.putExtra("resId", resId)
        setResult(RESULT_OK, _result)
        finish()
    }
}