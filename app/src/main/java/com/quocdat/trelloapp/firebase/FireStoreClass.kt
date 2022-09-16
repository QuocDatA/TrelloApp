package com.quocdat.trelloapp.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.quocdat.trelloapp.activities.MainActivity
import com.quocdat.trelloapp.activities.SignInActivity
import com.quocdat.trelloapp.activities.SignUpActivity
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

    fun signInUser(activity: Activity){
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
                        activity.updateNavigationUserDetails(loggedInUser)
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

    fun getCurrentUserID(): String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null)
            currentUserID = currentUser.uid.toString()

        return currentUserID
    }
}