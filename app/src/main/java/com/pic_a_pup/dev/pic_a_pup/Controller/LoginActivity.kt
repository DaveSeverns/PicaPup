package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.EMAIL
import com.pic_a_pup.dev.pic_a_pup.Utilities.PASSWORD
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sign_up_btn.setOnClickListener(this::signUpNewUser)
    }

    fun signUpNewUser(view: View){
        val toSignUpActivity = Intent(this, SignUpActivity::class.java)
        startActivity(toSignUpActivity)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        var userEmail = intent?.getStringArrayExtra(EMAIL)
        var password = intent?.getStringExtra(PASSWORD)
        email_edit_text.setText(userEmail.toString())
        pass_edit_text.setText(password.toString())
    }
}
