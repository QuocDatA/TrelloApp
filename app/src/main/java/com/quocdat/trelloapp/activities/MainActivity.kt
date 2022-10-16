package com.quocdat.trelloapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.snapshot.BooleanNode
import com.google.firebase.messaging.FirebaseMessaging
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.adapters.BoardItemAdapter
import com.quocdat.trelloapp.base.BaseActivity
import com.quocdat.trelloapp.firebase.FireStoreClass
import com.quocdat.trelloapp.models.Board
import com.quocdat.trelloapp.models.Users
import com.quocdat.trelloapp.utils.Constants
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        private const val MY_PROFILE_REQUEST_CODE = 1
        private const val CREATE_BOARD_REQUEST_CODE = 2
    }

    private lateinit var mUsername: String
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        setUpActionBar()
        nav_view.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(Constants.TRELLO_PREFERENCE
            , Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences
            .getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if (tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().loadUserData(this, true)
        }else{
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                task ->
                updateFCMToken(task.result)
            }
        }


        FireStoreClass().loadUserData(this, true)

        fab_create_board.setOnClickListener{
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUsername)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }

    private fun updateFCMToken(token: String){
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateUserProfileData(this, userHashMap)
    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()

        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadUserData(this, true)
    }

    fun populateBoardListToUI(boardList: ArrayList<Board>){
        hideProgressDialog()

        if (boardList.size > 0){
            rv_board_list.visibility = View.VISIBLE
            tv_no_board_available.visibility = View.GONE

            rv_board_list.layoutManager = LinearLayoutManager(this)
            rv_board_list.setHasFixedSize(true)

            val adapter = BoardItemAdapter(this, boardList)
            rv_board_list.adapter = adapter

            adapter.setOnClickListener(object : BoardItemAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentID)
                    startActivity(intent)
                }

            })

        }else{
            rv_board_list.visibility = View.GONE
            tv_no_board_available.visibility = View.VISIBLE
        }
    }

    fun updateNavigationUserDetails(user: Users, readBoardList: Boolean){

        mUsername = user.name

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(circle_image_view);
        tv_user_name.text = user.name

        if (readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardList(this)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FireStoreClass().loadUserData(this)
        }else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FireStoreClass().getBoardList(this)
        }
        else{
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
               startActivityForResult(Intent(this, MyProfileActivity::class.java),
                                        MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}