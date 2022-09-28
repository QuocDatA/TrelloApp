package com.quocdat.trelloapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.quocdat.trelloapp.activities.*
import com.quocdat.trelloapp.models.Board
import com.quocdat.trelloapp.models.Users
import com.quocdat.trelloapp.utils.Constants

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: Users){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARD)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Create Board Successfully!")
                Toast.makeText(activity, "Create Board Successfully!", Toast.LENGTH_LONG
                ).show()
                activity.createdBoardSuccessfully()
            }.addOnFailureListener{
                exception ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while creating board! ", exception)
            }
    }

    fun addUpdateTaskList(activity: TaskListActivity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARD)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Update TaskList Successfully!")
                activity.addUpdateTaskListSuccess()
            }.addOnFailureListener{
                    exception ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while updating taskList! ", exception)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data Updated")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_LONG
                ).show()
                activity.updateProfileSuccess()
            }.addOnFailureListener{
                e ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while creating a board")
                Toast.makeText(activity, "Error when creating the board", Toast.LENGTH_LONG
                ).show()
            }
    }

    fun loadUserData(activity: Activity, readBoardList: Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(Users::class.java)!!
                when(activity){
                    is SignInActivity ->{
                        activity.userSignInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDateInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener{
                e ->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.d("SignIn: ", "SignIn Failed!", e)
            }
    }

    //load data from firebase to view
    fun getMemberDetails(activity: MemberActivity, email: String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                    document ->
                if (document.documents.size >0){
                    val user = document.documents[0].toObject(Users::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }.addOnFailureListener{
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while getting user detail ")
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String){
        mFireStore.collection(Constants.BOARD)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val board = document.toObject(Board::class.java)!!
                board.documentID = document.id
                activity.boardDetails(board)

            }.addOnFailureListener{
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while creating a board")
            }
    }

    fun getMembersList(activity: MemberActivity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val userList: ArrayList<Users> = ArrayList()
                for (i in document.documents){
                    val user = i.toObject(Users::class.java)!!
                    userList.add(user)
                }
                activity.setUpMembersList(userList)
            }.addOnFailureListener{
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while getting a user")
            }
    }

    fun getBoardList(activity: MainActivity){
        mFireStore.collection(Constants.BOARD)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentID = i.id
                    boardList.add(board)
                }
                activity.populateBoardListToUI(boardList)
            }.addOnFailureListener{
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while creating a board")
            }
    }

    fun getCurrentUserID(): String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null)
            currentUserID = currentUser.uid.toString()

        return currentUserID
    }

    //update user to board
    fun assignedMemberToBoard(activity: MemberActivity, board: Board, user: Users){

        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARD)
            .document(board.documentID)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Assigned member to board")
                activity.assignedMemberSuccess(user)
            }.addOnFailureListener{
                    e ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while assigning to board")
            }
    }
}