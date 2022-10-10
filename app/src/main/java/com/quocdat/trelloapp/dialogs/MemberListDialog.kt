package com.quocdat.trelloapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.adapters.MemberListItemAdapter
import com.quocdat.trelloapp.models.Users
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class MemberListDialog(
    context: Context,
    private val title: String = "",
    private var list: ArrayList<Users>
): Dialog(context) {

    private var adapter: MemberListItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerview(view)
    }

    private fun setUpRecyclerview(view: View){
        view.tv_title.text = title

        view.rvList.layoutManager = LinearLayoutManager(context)
        adapter = MemberListItemAdapter(context, list)
        view.rvList.adapter = adapter

        adapter!!.onClickListener = object : MemberListItemAdapter.OnClickListener {
            override fun onClick(position: Int, users: Users, selected: String) {
                dismiss()
                onItemSelected(users, selected)
            }

        }
    }

    protected abstract fun onItemSelected(users: Users, action: String)
}