package com.example.game

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class GameActivity : AppCompatActivity(),
    Mover {
    private val db = FirebaseDatabase.getInstance()
    private val dbRef = db.reference
    private lateinit var gameId: String
    private lateinit var uid: String
    private var initiator: Boolean = false
    private var whitePlayer: Boolean = true
    private lateinit var inputQue: DatabaseReference
    private lateinit var chess:ChessBoardView
    private lateinit var txtStep: TextView
    private lateinit var msgBox: MessageBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setSupportActionBar(findViewById(R.id.toolbar))
        initiator = intent.getBooleanExtra("initiator", false)
        uid = intent.getStringExtra("uid")!!
        gameId = intent.getStringExtra("gameId")!!
        (findViewById(R.id.btn_draw) as Button).isEnabled=false;
        var txt = findViewById(R.id.textColor) as TextView
        txtStep = findViewById(R.id.textInfo) as TextView
        txt.isVisible = false;
        val u = UserProfile()
        u.ReadData(null, {fillP1(u)})
        val ref1 = dbRef.child("Games").child(gameId).child("Player1")
        val ref2 = dbRef.child("Games").child(gameId).child("Player2")
        if (initiator) {
            inputQue = ref2
            setListeners(ref1)
        }
        else {
            inputQue = ref1
            setListeners(ref2)
        }
        chess = findViewById<ChessBoardView>(R.id.chessboard)
        chess.listener = this
        Log.i("CHESS", "gameid="+gameId)
        var ref = dbRef.child("Waiting").child(gameId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val s = p0.value as String
                    if (initiator && s.contains("j:")) {
                        Go(txtStep, txt, s.contains("w:"), s)
                    }
                    if (!initiator && s.contains("i:")) {
                        Go(txtStep, txt, s.contains("b:"), s) // set while if initiator chose black
                    }
                }
            }
        })

    }

    private fun fillP1(u:UserProfile) {
        findViewById<TextView>(R.id.txt_p1).setText(u.Name);
        findViewById<ImageView>(R.id.img_p1).setImageResource(u.AvatarId);
    }

    private fun fillP2(u:UserProfile) {
        findViewById<TextView>(R.id.txt_p2).setText(u.Name);
        findViewById<ImageView>(R.id.img_p2).setImageResource(u.AvatarId);
    }
    private fun processUid(str:String) {
        val ar = str.split(':')
        if (ar.size == 2 && ar[0] == "uid") {
            val u = UserProfile()
            u.ReadData(ar[1], {fillP2(u)})
        }
    }
    private fun setListeners(ref:DatabaseReference) {
        ref.child("Commands").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val data = p0.value as String
                    when(data) {
                        "concede" -> receiveEnd()
                        "propose_draw" -> receiveDraw(Mover.Command.propose_draw)
                        "accept_draw" -> receiveDraw(Mover.Command.accept_draw)
                        "refuse_draw" -> receiveDraw(Mover.Command.refuse_draw)
                    }
                }
            }
        })
        ref.child("Steps").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val data = p0.value as String
                    val steps = data.split('-')
                    Log.i("CHESS", "MOVE: " + data)
                    if (steps[0] == "m") {// command - move
                        chess.doMove(steps[1], steps[2])
                        manageStep(true)
                    }
                    else if(steps[0] == "d") // delete a figure
                        chess.doRemove(steps[1])
                }
            }
        })
    }
    private fun Go(txtStep: TextView,txt: TextView, white:Boolean, str: String) {
        whitePlayer = white
        var idxb = 0
        var idxe = 0
        if (initiator) {
            idxb = str.indexOf("uidj:[") // joiner id
            idxe = str.indexOf("]", idxb)
        }
        else {
            idxb = str.indexOf("uidi:[") // initator id
            idxe = str.indexOf("]", idxb)
        }
        if (idxb > 0) {
            val userUid = str.substring(idxb + 6, idxe)
            val u = UserProfile()
            u.ReadData(userUid, {fillP2(u)})
        }
        if (whitePlayer) {
            txt.text = getString(R.string.txt_play_white)
            txtStep.text = getString(R.string.txtInfo_do)
            txtStep.setTextColor(getResources().getColor(R.color.red_200))
        }
        else {
            txt.text = getString(R.string.txt_play_black)
            txtStep.text = getString(R.string.txtInfo_wait)
            txtStep.setTextColor(getResources().getColor(R.color.teal_200))
        }
        txt.isVisible = true
        (findViewById(R.id.btn_draw) as Button).isEnabled=true;
        (findViewById<TextView>(R.id.lblWait)).isVisible = false
        Handler().postDelayed({
            val chess = findViewById<ChessBoardView>(R.id.chessboard)
            chess.init(applicationContext, whitePlayer)
            chess.Disabled = false
        }, 200)
    }

    private fun manageStep(take: Boolean)
    {
        if (take) {
            txtStep.text = getString(R.string.txtInfo_do)
            txtStep.setTextColor(getResources().getColor(R.color.red_200))
        }
        else {
            txtStep.text = getString(R.string.txtInfo_wait)
            txtStep.setTextColor(getResources().getColor(R.color.teal_200))
        }
        chess.Disabled = !take
    }

    private fun processDraw(accept:Boolean) {
        if (accept) {
            sendDraw(Mover.Command.accept_draw)
            finish()
        }
        else
        {
            sendDraw(Mover.Command.refuse_draw)
            chess.Disabled = false;
        }
    }

    private fun receiveDraw(cmd: Mover.Command) {
        chess.Disabled = true;
        if (cmd == Mover.Command.propose_draw) {
            msgBox = MessageBox()
            msgBox.showYesNo(chess.context, getString(R.string.txt_request), getString(R.string.propose_draw), { processDraw(true) }, {processDraw(false)})
        }
        else if (cmd == Mover.Command.accept_draw) {
            val ref1 = dbRef.child("Games").child(gameId).child("info")
            ref1.child("result").setValue(GameStatus.Draw)
            finish()
        }
        else {
            msgBox.cancel()
            chess.Disabled = false
        }
    }

    private fun receiveEnd() {
        chess.Disabled = true;
        msgBox = MessageBox()
        val ref1 = dbRef.child("Games").child(gameId).child("info")
        ref1.child("result").setValue(uid)
        msgBox.show(chess.context, getString(R.string.txt_exist), getString(R.string.you_win), {finish()})
    }
    private fun sendEnd() {
        chess.Disabled = true;
        inputQue.child("Commands").setValue(Mover.Command.concede)
        msgBox = MessageBox()
        msgBox.show(chess.context, getString(R.string.txt_exist), getString(R.string.you_lose), {finish()})
    }

    private fun sendUid() {
        inputQue.child("Commands").setValue("uid:" + uid)
    }

    private fun sendDraw(cmd :Mover.Command) {
        chess.Disabled = true;
        inputQue.child("Commands").setValue(cmd)
        if (cmd == Mover.Command.propose_draw) {
            msgBox = MessageBox()
            msgBox.showModal(chess.context, getString(R.string.txt_request), getString(R.string.request_draw))
        }
    }

    fun onClickConcede(view: View) {
      DoCommand(Mover.Command.concede)
    }
    fun onClickDraw(view: View) {
      DoCommand(Mover.Command.propose_draw)
    }

    fun onClickYes(view: View) {
      DoCommand(Mover.Command.accept_draw)
    }
    fun onClickNo(view: View) {
      DoCommand(Mover.Command.refuse_draw)
    }


    override fun DoMove(currentPosition: String, nextPosition: String) {
        inputQue.child("Steps").setValue("m-"+ currentPosition + "-" + nextPosition)
        manageStep(false)
    }

    override fun DoRemove(position: String) {
        inputQue.child("Steps").setValue("d-"+ position)
    }

    override fun DoCommand(cmd: Mover.Command) {
        when(cmd) {
            Mover.Command.concede -> sendEnd()
            else -> sendDraw(cmd)
        }
    }

}