package com.quocdat.trelloapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.auth.User
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.models.Users
import kotlinx.android.synthetic.main.item_members.view.*

class MemberListItemAdapter(
    private val context: Context,
    private var list: ArrayList<Users>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}