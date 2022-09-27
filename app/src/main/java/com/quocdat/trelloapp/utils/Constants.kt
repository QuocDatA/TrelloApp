package com.quocdat.trelloapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.quocdat.trelloapp.activities.MyProfileActivity

object Constants {
    const val USERS: String = "users"
    const val BOARD: String = "board"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAIL: String = "board_detail"

    const val IMAGE: String = "image"
    const val MOBILE: String = "mobile"
    const val NAME: String = "name"
    const val DOCUMENT_ID: String = "documentId"

    const val ASSIGNED_TO: String = "assignedTo"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2


    fun getFileExtension(activity: Activity, uri: Uri?) : String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    //Chooser for the Profile Image
    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}

