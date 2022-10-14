package com.quocdat.trelloapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.auth.User
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.models.Users
import com.quocdat.trelloapp.utils.Constants
import kotlinx.android.synthetic.main.item_members.view.*

class MemberListItemAdapter(
    private val context: Context,
    private var list: ArrayList<Users>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_members, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_list_members_image)

            holder.itemView.tv_members_name.text = model.name
            holder.itemView.tv_members_email.text = model.email

            if (model.selected){
                holder.itemView.iv_selected_member.visibility = View.VISIBLE
            }else{
                holder.itemView.iv_selected_member.visibility = View.GONE
            }

            if (onClickListener != null){
                if (model.selected){
                    onClickListener!!.onClick(position, model, Constants.UNSELECT)
                    Log.i("TAG:", "onBindViewHolder: ")
                }else{
                    onClickListener!!.onClick(position, model, Constants.SELECT)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, users: Users, action: String)
    }
}