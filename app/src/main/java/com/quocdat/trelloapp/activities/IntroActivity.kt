package com.quocdat.trelloapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btnSignInIntro.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btnSignUpIntro.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }
}