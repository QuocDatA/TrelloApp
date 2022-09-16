package com.quocdat.trelloapp.base

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.quocdat.trelloapp.R
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressBar: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text: String){
        mProgressBar = Dialog(this)

        //Set layout resource
        mProgressBar.setContentView(R.layout.dialog_progress)
        mProgressBar.tv_progress_text.text = text

        //start dialog and display it on screen
        mProgressBar.show()
    }

    fun hideProgressDialog(){
        mProgressBar.dismiss()
    }

    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_LONG
        ).show()

        Handler().postDelayed({doubleBackToExitPressedOnce}, 2000)
    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,
            R.color.snackBar_error_color))
        snackBar.show()
    }
}