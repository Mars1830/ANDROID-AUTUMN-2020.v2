package com.example.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN_GOOGLE = 102
    private val RC_SIGN_IN = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (auth.currentUser != null)
            goToMain();

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
                .requestIdToken("105726806827-bemfin93gfsr2v05qbgrea4ndcpk1djp.apps.googleusercontent.com")
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        var btnSignIn = findViewById(R.id.btnGoogleSignin) as SignInButton;
        btnSignIn.setOnClickListener { view ->
            onClickGoogle(view)
        }
    }

    fun onClickGoogle(view: View) {
        val signInGoogleIntent = googleSignInClient.signInIntent
        startActivityForResult(signInGoogleIntent, RC_SIGN_IN_GOOGLE)
    }

   private fun createUser(g: GoogleSignInAccount) {
       var user = UserProfile()
       var authUser = auth.currentUser!!
       user.uid = authUser.uid!!
       user.Name = g.givenName!!
       user.Email = g.email!!
       user.AvatarId = 0
       user.WriteDataIfNotExists()
   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                goToMain()
            }
            RC_SIGN_IN_GOOGLE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val googleAccount = task.getResult(ApiException::class.java)
                    if (googleAccount != null) {
                        val credential =
                            GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                        auth.signInWithCredential(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                createUser(googleAccount!!)
                                goToMain()
                            } else {
                                Toast.makeText(
                                    applicationContext, R.string.err_google_signin,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                } catch (e: ApiException) {
                    Toast.makeText(
                        applicationContext,
                        "${getString(R.string.err_google_enter)}: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun onClickCreateLogin(view: View) {
        val intent = Intent(this, CreateAccount::class.java)
        var email = (findViewById(R.id.edtLogin) as EditText).text.toString()
        val password = (findViewById(R.id.edtPassword) as EditText).text.toString()
        intent.putExtra("email", email)
        intent.putExtra("pwd", password)
        intent.putExtra("create", true)
        startActivityForResult(intent, RC_SIGN_IN)
    }

    fun onClickLogin(view: View) {
            var email = (findViewById(R.id.edtLogin) as EditText).text.toString()
            val password = (findViewById(R.id.edtPassword) as EditText).text.toString()
            if (email.isEmpty()) {
                Toast.makeText(
                    applicationContext, R.string.err_empty_email,
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            var fail = false
            auth.signInWithEmailAndPassword(email, password).addOnFailureListener { e ->
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
                goToMain()
            }
        }


    private fun goToMain() {
        val currentUser = auth.currentUser
        if(currentUser == null) {
            Toast.makeText(applicationContext,R.string.err_no_login, Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, StartActivity::class.java)
        intent.putExtra("uid", currentUser.uid)
        startActivity(intent)
        finish()
    }
}