package com.quocdat.trelloapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.adapters.LabelColorListItemsAdapter
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class LabelColorListDialog(
    context: Context,
    private val title: String = "",
    private var list: ArrayList<String>,
    private var mSelectedColor: String = ""
): Dialog(context) {

    private var adapter: LabelColorListItemsAdapter? = null

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
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        view.rvList.adapter = adapter

        adapter!!.onItemClickListener = object : LabelColorListItemsAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
                Log.i("ClickItem: ", "onClick2: ")
            }

        }
    }

    protected abstract fun onItemSelected(color: String)
}