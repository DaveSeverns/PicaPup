package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    private  var  userEmailFromIntent: String = ""
    private  var  passwordFromIntent: String = ""
    private lateinit var mAuth: FirebaseAuth
    private var mContext: Context = this
    private lateinit var mFirebaseManager: FirebaseManager
    private lateinit var mListener: FirebaseAuth.AuthStateListener
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var userName:String? = null
    private var phoneNumber:String? = null
    private  var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        pref = getSharedPreferences(USER_PREF_FILE,Context.MODE_PRIVATE)
        userName = pref.getString(PREF_USER_NAME_KEY,"default")
        phoneNumber = pref.getString(PREF_USER_PHONE_KEY,"420")



        mFirebaseManager = FirebaseManager(this)
        sign_up_btn.setOnClickListener(this::signUpNewUser)
        if((userEmailFromIntent!!.isNotEmpty())&& (passwordFromIntent!!.isNotEmpty())){
            email_edit_text.setText(userEmailFromIntent)
            pass_edit_text.setText(passwordFromIntent)
        }

        login_btn.setOnClickListener(this::logInUser)

        mListener = FirebaseAuth.AuthStateListener {
            firebaseUser = it.currentUser
            var nameNotDef: Boolean = userName.equals("default")
            var phoneNotDef = phoneNumber.equals("420")
            if(firebaseUser != null){

                editor = pref.edit()
                //while(nameNotDef || phoneNotDef  ){
                //    if (userName.equals("default")){
//
                //        val editText = EditText(this)
                //        AlertDialog.Builder(this)
                //                .setTitle("Update Username")
                //                .setView(editText)
                //                .setPositiveButton("Submit", object: DialogInterface.OnClickListener{
                //                    override fun onClick(dialog: DialogInterface?, which: Int) {
                //                        editor.putString(PREF_USER_NAME_KEY,editText.text.toString()).apply()
                //                        nameNotDef = false
                //                    }
//
                //                }).show()
                //    }
                //    if(phoneNumber.equals("420")){
                //        val secondEditText = EditText(this)
                //        AlertDialog.Builder(this)
                //                .setTitle("Update Phone Number")
                //                .setView(secondEditText)
                //                .setPositiveButton("Submit", object: DialogInterface.OnClickListener{
                //                    override fun onClick(dialog: DialogInterface?, which: Int) {
                //                        editor.putString(PREF_USER_PHONE_KEY,secondEditText.text.toString()).apply()
                //                        phoneNotDef = false
                //                    }
//
                //                }).show()
                //    }
                //}
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

            mFirebaseManager.logUserIntoFirebase(currentEmail, currentPw)

            Log.d(AUTH_TAG," logInUser Called")

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

    override fun onBackPressed() {

    }
}
