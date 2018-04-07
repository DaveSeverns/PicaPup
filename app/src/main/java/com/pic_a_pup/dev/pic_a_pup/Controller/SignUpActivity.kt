package com.pic_a_pup.dev.pic_a_pup.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pic_a_pup.dev.pic_a_pup.Model.DogLover
import com.pic_a_pup.dev.pic_a_pup.R
import com.pic_a_pup.dev.pic_a_pup.Utilities.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var mUtility: Utility
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var mContext = this
    private lateinit var emailText: EditText
    private lateinit var userNameText: EditText
    private lateinit var pwTextOne: EditText
    private lateinit var confirmPWText: EditText
    private lateinit var phoneNumText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        FirebaseApp.initializeApp(mContext)
        emailText = findViewById<EditText>(R.id.email_sign_up)
        userNameText = findViewById<EditText>(R.id.user_name_signup)
        pwTextOne = findViewById<EditText>(R.id.pw_one_signup)
        confirmPWText = findViewById<EditText>(R.id.pw_two_signup)
        phoneNumText = findViewById<EditText>(R.id.phone_number_signup)
        mUtility = Utility(mContext)
        mDatabase = FirebaseDatabase.getInstance().reference.child(USER_TABLE)
        mFirebaseAuth = FirebaseAuth.getInstance()
        if(pwTextOne.text.toString().trim().equals(confirmPWText.text.toString().trim())){
            submit_button.setOnClickListener(this::createAccount)

        }else{
            mUtility.showToast("Passwords do not match!")
        }

    }

    fun createAccount(view: View){
        val userName = userNameText.text.toString().trim()
        val userEmail = emailText.text.toString().trim()
        val userPassword = pwTextOne.text.toString().trim()
        val phoneNumber = phoneNumText.text.toString().trim()


        if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)){
            if(mUtility.isValidEmail(userEmail) && mUtility.isValidPassword(userPassword)){
                mFirebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this,
                        OnCompleteListener {
                            if(it.isSuccessful){
                                Log.d(AUTH_TAG,"Successully added user through Firebase Authentication")
                                val user = mFirebaseAuth.currentUser
                                val userId = user?.uid
                                if(user != null){
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(userName).build()
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener{
                                                if(it.isSuccessful){
                                                    Log.i("Profile Status: ", "Updated")
                                                }
                                            }
                                }
                                val currentUserDb = mDatabase.child(userId)
                                currentUserDb.child("username").setValue(userName)
                                currentUserDb.child("uid").setValue(userId)
                                currentUserDb.child("phoneNumber").setValue(phoneNumber)
                                val dogLover = DogLover(userId!!,userName,null,phoneNumber,null)
                                val pref = getSharedPreferences(USER_PREF_FILE,Context.MODE_PRIVATE)
                                val editor = pref.edit()
                                editor.putString(PREF_USER_NAME_KEY, userName)
                                editor.putString(PREF_USER_PHONE_KEY,phoneNumber)
                                editor.apply()


                                val authBackToLoginActivity = Intent(this,LoginActivity::class.java)
                                authBackToLoginActivity.putExtra(EMAIL, userEmail)
                                authBackToLoginActivity.putExtra(PASSWORD, userPassword)
                                startActivity(authBackToLoginActivity)

                            }else{
                                it.addOnFailureListener { exception ->
                                    Log.d(AUTH_TAG, "Failed to add User")
                                }
                            }
                        }

                )
            }else if(!mUtility.isValidEmail(userEmail)){
                mUtility.showToast("Enter a valid email address, please.")
            }else{
                mUtility.showToast("Enter a valid password.")
            }
        }

    }
}
