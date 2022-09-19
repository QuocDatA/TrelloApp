package com.quocdat.trelloapp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.base.BaseActivity
import com.quocdat.trelloapp.firebase.FireStoreClass
import com.quocdat.trelloapp.models.Users
import com.quocdat.trelloapp.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageUri: Uri?= null
    private var mProfileImageURL: String = ""
    private lateinit var mDetailsUser: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setUpActionBar()
        FireStoreClass().loadUserData(this)

        iv_user_image.setOnClickListener{
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                //TODO SHOW IMAGE CHOOSER
                showImageChooser()
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_update.setOnClickListener {
            if (mSelectedImageUri != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //TODO SHOW IMAGE CHOOSER
                showImageChooser()
            }
        }else{
            Toast.makeText(this, "Oops! You just denied the permission for the storage. You can allow it from setting!",
                            Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageUri = data.data
            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_user_image);
            }catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun updateProfileSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    //update user profile to storage
    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mDetailsUser.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (et_mobile_my_profile.text.toString() != mDetailsUser.mobile.toString()){
            userHashMap[Constants.MOBILE] = et_mobile_my_profile.text.toString().toLong()
        }

        if (et_name_my_profile.text.toString() != mDetailsUser.name){
            userHashMap[Constants.NAME] = et_name_my_profile.text.toString()
        }

        FireStoreClass().updateUserProfileData(this, userHashMap)
    }

    //Uploading Image to Storage
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USERNAME" + System.currentTimeMillis()
                        + "." + getFileExtension(mSelectedImageUri!!))

            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL",
                            taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()


                    //TODO UpdateUserProfileData
                    updateUserProfileData()

                }
            }.addOnFailureListener{
                    exception ->
                Toast.makeText(this@MyProfileActivity,
                    exception.message, Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
        }
    }

    private fun getFileExtension(uri: Uri?) : String?{
        return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    //Chooser for the Profile Image
    fun showImageChooser(){
        val galleryIntent = Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun setUserDateInUI(user: Users){

        mDetailsUser = user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image);
        et_name_my_profile.setText(user.name)
        et_email_my_profile.setText(user.email)
        if (user.mobile != 0L){
            et_mobile_my_profile.setText(user.mobile.toString())
        }
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