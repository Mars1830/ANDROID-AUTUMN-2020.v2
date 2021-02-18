package com.example.game

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class CreateAccount : AppCompatActivity() {

    private val PICK_IMAGE = 100
    private lateinit var img: ImageView
    private lateinit var emailCtrl: EditText
    private lateinit var nameCtrl: EditText
    private lateinit var pwdCtrl: EditText
    private var create: Boolean = true;
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        img = findViewById<ImageView>(R.id.img_avatar)
        emailCtrl = findViewById<EditText>(R.id.edt_email)
        nameCtrl = findViewById<EditText>(R.id.edt_Name)
        pwdCtrl = findViewById<EditText>(R.id.edt_pwd)
        pwdCtrl.setText(intent.getStringExtra("pwd"))
        emailCtrl.setText(intent.getStringExtra("email"))
        create = intent.getBooleanExtra("create", true)
        if (create) {
            actionBar?.setTitle(R.string.title_activity_create_account)
            findViewById<Button>(R.id.btn_save).setText(R.string.btn_save_create)
            pwdCtrl.setText(intent.getStringExtra("pwd"))
            emailCtrl.setText(intent.getStringExtra("email"))
        } else {
            actionBar?.setTitle(R.string.title_activity_editaccount)
            findViewById<Button>(R.id.btn_save).setText(R.string.btn_save_save)
            pwdCtrl.isEnabled = false;
            findViewById<EditText>(R.id.edt_pwd2).isEnabled = false;
            emailCtrl.isEnabled = false
            val user = UserProfile()
            user.ReadData(null, { fillParams(user) });
        }
    }

    private fun fillParams(u:UserProfile)  {
        if (u.AvatarId > 0)
            img.setImageResource(u.AvatarId.toInt())
        nameCtrl.setText(u.Name)
        emailCtrl.setText(u.Email)


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

    fun onImageClick(view: View) {
        val intent = Intent(this, AvatarListActivity::class.java)
        startActivityForResult(intent, PICK_IMAGE);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            var res = data?.getIntExtra("resId", 0)
            img.tag = res;
            img.setImageResource(res!!)
        }
    }

    fun onBtnClick(view: View) {

        var email = emailCtrl.text.toString().trim()
        var name = nameCtrl.text.toString().trim()
        val password = pwdCtrl.text.toString().trim()
        val password2 = findViewById<EditText>(R.id.edt_pwd2).text.toString().trim()

        if (email.isNullOrBlank() || name.isNullOrBlank() || (password.isNullOrBlank() && create)) {
            Toast.makeText(
                    applicationContext, R.string.no_empty_name,
                    Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!password.equals(password2)) {
            Toast.makeText(
                    applicationContext, R.string.password_mismatch,
                    Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (create) {
            var fail = false
            auth.createUserWithEmailAndPassword(email, password).addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                fail = true
            }.addOnCompleteListener { task ->
                if (fail)
                    return@addOnCompleteListener
                if (!task.isSuccessful) {
                    Toast.makeText(
                            applicationContext,
                            "Error: ${task.result.toString()}",
                            Toast.LENGTH_LONG
                    ).show()
                    return@addOnCompleteListener
                }
                var user = UserProfile()
                var authUser = auth.currentUser!!
                user.uid = authUser.uid!!
                user.Name = nameCtrl.text.toString().trim()
                user.Email = authUser.email!!
                user.AvatarId = if (img.tag != null) img.tag as Int else 0
                user.WriteData(null, { } )
                setResult(RESULT_OK)
                finish()
            }
        }
        else {
            var user = UserProfile()
            var authUser = auth.currentUser!!
            user.uid = authUser.uid!!
            user.Name = nameCtrl.text.toString().trim()
            user.Email = authUser.email!!
            user.AvatarId = if (img.tag != null) img.tag as Int else 0
            user.WriteData(null, {
                setResult(RESULT_OK)
                finish()
            })
        }
    }
}
