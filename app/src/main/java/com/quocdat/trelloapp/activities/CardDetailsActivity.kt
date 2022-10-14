package com.quocdat.trelloapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.adapters.CardMemberListItemAdapter
import com.quocdat.trelloapp.base.BaseActivity
import com.quocdat.trelloapp.dialogs.LabelColorListDialog
import com.quocdat.trelloapp.dialogs.MemberListDialog
import com.quocdat.trelloapp.firebase.FireStoreClass
import com.quocdat.trelloapp.models.*
import com.quocdat.trelloapp.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardListPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMemberDetailsList: ArrayList<Users>
    private var mSelectedDueDateMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        getIntentData()
        setUpActionBar()

        et_name_card_details.setText(mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardListPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].labelColor

        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this, "Please enter a card name!", Toast.LENGTH_LONG).show()
            }
        }

        tv_select_color.setOnClickListener {
            listColorDialog()
            Log.i("ClickItem: ", "onClick1: ")
        }

        tv_select_members.setOnClickListener {
            memberListDialog()
        }

        setUpSelectedMemberList()

        mSelectedDueDateMilliSeconds = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardListPosition].dueDate

        if (mSelectedDueDateMilliSeconds > 0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            tv_due_date.text = selectedDate
        }

        tv_due_date.setOnClickListener {
            showDatePicker()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                alertDialogForDeleteCard(mBoardDetails
                    .taskList[mTaskListPosition]
                    .cards[mCardListPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val mDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val mMonthOfYear = if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else
                    "${monthOfYear + 1}"
                val selectedDate = "$mDayOfMonth/$mMonthOfYear/$year"
                tv_due_date.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun setUpSelectedMemberList(){
        val cardAssignMemberList = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardListPosition].assignedTo

        val selectedMemberList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMemberDetailsList.indices){
            for (j in cardAssignMemberList){
                if (mMemberDetailsList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMemberDetailsList[i].id,
                        mMemberDetailsList[i].image,
                    )
                    selectedMemberList.add(selectedMember)
                }

            }
        }

        if (selectedMemberList.size > 0){
            selectedMemberList.add(SelectedMembers("", ""))
            tv_select_members.visibility = View.VISIBLE
            //rv_selected_member_list.visibility = View.VISIBLE

            rv_selected_member_list.layoutManager = GridLayoutManager(this, 6)

            val adapter = CardMemberListItemAdapter(this, selectedMemberList, true)
            rv_selected_member_list.adapter = adapter

            adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                override fun onClick() {
                    memberListDialog()
                    Log.i("ClickItem: ", "onClick1: ")
                }
            })
        }else{
            tv_select_members.visibility = View.VISIBLE
            rv_selected_member_list.visibility = View.GONE
        }
    }

    private fun memberListDialog(){
        val cardAssignedMemberList = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardListPosition].assignedTo

        if (cardAssignedMemberList.size > 0){
            for (i in mMemberDetailsList.indices){
                for (j in cardAssignedMemberList){
                    if (mMemberDetailsList[i].id == j){
                        mMemberDetailsList[i].selected = true
                    }
                }
            }
        }else{
            for (i in mMemberDetailsList.indices){
                mMemberDetailsList[i].selected = false
            }
        }

        val listDialog = object : MemberListDialog(
            this,
            resources.getString(R.string.select_member),
            mMemberDetailsList
        )
        {
            override fun onItemSelected(users: Users, action: String) {
                if (action == Constants.SELECT){
                    if (!mBoardDetails.taskList[mTaskListPosition]
                            .cards[mCardListPosition].assignedTo.contains(users.id))
                    {
                        mBoardDetails.taskList[mTaskListPosition]
                            .cards[mCardListPosition].assignedTo.add(users.id)
                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition]
                        .cards[mCardListPosition].assignedTo.remove(users.id)

                    for (i in mMemberDetailsList.indices){
                        if (mMemberDetailsList[i].id == users.id){
                            mMemberDetailsList[i].selected = false
                        }
                    }
                }
                setUpSelectedMemberList()
            }
        }
        listDialog.show()

    }


    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor(){
        tv_select_color.text = ""
        tv_select_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun listColorDialog(){
        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorListDialog(
            this@CardDetailsActivity,
            resources.getString(R.string.str_select_label_color),
            colorsList,
            mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }

        listDialog.show()
    }

    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.yes)){
                dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){
            dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteCard(){
        val cardList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardList.removeAt(mCardListPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(mBoardDetails.taskList.size - 1)

        taskList[mTaskListPosition].cards = cardList
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    private fun updateCardDetails(){
        val card = Card(
            et_name_card_details.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )
        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition] = card

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_card_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
            actionBar.title = mBoardDetails
                .taskList[mTaskListPosition]
                .cards[mCardListPosition].name
        }
        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(
                Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardListPosition = intent.getIntExtra(
                Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMemberDetailsList = intent.getParcelableArrayListExtra(
                Constants.BOARD_MEMBERS_LIST)!!
        }
    }
}