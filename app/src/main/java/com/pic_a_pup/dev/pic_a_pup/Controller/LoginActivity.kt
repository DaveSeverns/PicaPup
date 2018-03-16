package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.EMAIL
import com.pic_a_pup.dev.pic_a_pup.Utilities.PASSWORD
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private  var  userEmailFromIntent: String = ""
    private  var  passwordFromIntent: String = ""
    private lateinit var mAuth: FirebaseAuth
    private var mContext: Context = this
    private lateinit var mListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        sign_up_btn.setOnClickListener(this::signUpNewUser)
        if((userEmailFromIntent!!.isNotEmpty())&& (passwordFromIntent!!.isNotEmpty())){
            email_edit_text.setText(userEmailFromIntent)
            pass_edit_text.setText(passwordFromIntent)
        }

        mListener = FirebaseAuth.AuthStateListener {
            var user = it.currentUser
            if(user != null){
                val toFeedActivity = Intent(this, HomeFeedActivity::class.java)
                startActivity(toFeedActivity)
            }
        }
    }

    fun signUpNewUser(view: View){
        val toSignUpActivity = Intent(this, SignUpActivity::class.java)
        startActivity(toSignUpActivity)
    }

    fun logInUser(view: View){
        var currentEmail = email_edit_text.text.toString().trim()
        var currentPw = pass_edit_text.text.toString().trim()

        if(!TextUtils.isEmpty(currentEmail) && !TextUtils.isEmpty(currentPw)){
            mAuth.signInWithEmailAndPassword(currentEmail,currentPw)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        userEmailFromIntent= intent!!.getStringExtra(EMAIL)
        passwordFromIntent = intent.getStringExtra(PASSWORD)

    }


    override fun onPause() {
        super.onPause()
        mAuth.removeAuthStateListener(mListener)
    }

    override fun onResume() {
        super.onResume()
        mAuth.addAuthStateListener(mListener)
    }
}
