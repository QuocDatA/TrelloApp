package com.quocdat.trelloapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.base.BaseActivity
import com.quocdat.trelloapp.firebase.FireStoreClass
import com.quocdat.trelloapp.models.Users
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setUpActionBar()

        btn_sign_in.setOnClickListener {
            signInRegisterUser()
        }
    }

    fun userSignInSuccess(user: Users){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }
        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisterUser(){
        val email: String = et_email_sign_in.text.toString().trim{ it <= ' '}
        val password: String = et_password_sign_in.text.toString().trim{ it <= ' '}
        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                task ->
                hideProgressDialog()
                if (task.isSuccessful){
                    FireStoreClass().loadUserData(this)
                }else{
                    Toast.makeText(this, "Sign In Failed!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(email) ->{
                showErrorSnackBar("Please enter an email address!")
                false
            }
            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please enter password!")
                false
            }else -> true
        }
    }
}