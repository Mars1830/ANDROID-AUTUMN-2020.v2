package com.example.game

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class StartActivity : AppCompatActivity() {
    private val db = FirebaseDatabase.getInstance()
    private val dbRef = db.reference
    private val user = FirebaseAuth.getInstance().currentUser
    private var gameId: String = ""
    private lateinit var editCode : EditText
    private lateinit var btnNext : Button
    private lateinit var switch: Switch
    private var initiator: Boolean = false;
    private val PROFILE: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        editCode = (findViewById(R.id.edtCode ) as EditText)
        editCode.addTextChangedListener(nextTextWatcher)
        btnNext = (findViewById(R.id.btnNext ) as Button)
        switch = (findViewById(R.id.switch_white ) as Switch)
    }

    fun onClickGenerate(view: View) {
        val ref = dbRef.child("Waiting").push()
        ref.setValue("s")
        gameId = ref.key!!
        editCode.setText(gameId)
    }

    fun onClickNext(view: View) {
        val initiator = !gameId.isNullOrBlank()
        if (!initiator) {// join - get the value from the editor
            gameId = editCode.getText().toString().trim();
            if (gameId.isNullOrBlank() )
                return;
        }

        var ref = dbRef.child("Waiting").child(gameId)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    var s = p0.value as String
                    if (initiator) {
                        s += "i:" + if (switch.isChecked) "w:" else "b:" // initiator  and color he selected
                        s += "uidi:[" + user!!.uid!!+ "]"
                    }
                    else {
                        s += "j:" // joiner
                        s += "uidj:[" + user!!.uid!! + "]"
                    }
                    ref.setValue(s)
                    val ref2 = dbRef.child("Games").child(gameId).child("info")
                    if (initiator) {
                        ref2.child("Date").setValue(Date())
                        ref2.child("Color").setValue(if (switch.isChecked) "white" else "black")
                        ref2.child("Player1").setValue(user!!.uid!!)
                    }
                    else
                        ref2.child("Player2").setValue(user!!.uid!!)
                    beginTheGame(initiator);
                } else {
                    btnNext.isEnabled = false
                    gameId = ""
                    Toast.makeText(
                            applicationContext,R.string.txt_wrong_step,
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }

    fun beginTheGame(creator: Boolean) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("initiator", creator)
        intent.putExtra("gameId", gameId)
        intent.putExtra("uid", user!!.uid!!)
        intent.putExtra("white", switch.isChecked)
        startActivity(intent)
        //finish()
    }

    fun onClickLogout(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val nextTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val code: String = editCode.getText().toString().trim()
            btnNext.setEnabled(!code.isEmpty())
        }

        override fun afterTextChanged(s: Editable) {}
    }

    fun runStats() {
        val intent = Intent(this, StatsActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_start, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_exit -> onClickLogout(item.actionView)
            R.id.action_stat -> runStats()
            R.id.action_profile ->runProfile()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PROFILE)
               (supportFragmentManager.findFragmentById(R.id.fragment2) as UserFragment).reload()
    }

    private fun runProfile() {
        val intent = Intent(this, CreateAccount::class.java)
        intent.putExtra("create", false)
        startActivityForResult(intent, PROFILE)
    }
}