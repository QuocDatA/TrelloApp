package com.quocdat.trelloapp.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.adapters.MemberListItemAdapter
import com.quocdat.trelloapp.base.BaseActivity
import com.quocdat.trelloapp.firebase.FireStoreClass
import com.quocdat.trelloapp.models.Board
import com.quocdat.trelloapp.models.Users
import com.quocdat.trelloapp.utils.Constants
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.dialog_add_member.*

class MemberActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mAssignedMembersList: ArrayList<Users> = ArrayList()
    private var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        setUpActionBar()

        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getMembersList(this, mBoardDetails.assignedTo)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogAddMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private fun dialogAddMember(){
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_add_member)
        dialog.tv_add_member.setOnClickListener {
            val email = dialog.et_email_add_member.text.toString()
            if (email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this, email)
            }else{
                Toast.makeText(this, "Please enter members email address!", Toast.LENGTH_LONG
                ).show()
            }
        }
        dialog.tv_cancel_member.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //add user by id into board
    fun memberDetails(user: Users){
        mBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignedMemberToBoard(this, mBoardDetails, user)
    }

    fun assignedMemberSuccess(user: Users){
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade = true

        setUpMembersList(mAssignedMembersList)
    }

    fun setUpMembersList(list: ArrayList<Users>){

        mAssignedMembersList = list

        hideProgressDialog()

        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)

        val adapter = MemberListItemAdapter(this, list)
        rv_members_list.adapter = adapter
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_members_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
            actionBar.title = resources.getString(R.string.members)
        }
        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }
}