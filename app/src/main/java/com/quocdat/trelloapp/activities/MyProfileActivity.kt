package com.quocdat.trelloapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class MyProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
            actionBar.title = resources.getString(R.string.nav_my_profile)
        }
        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }
}