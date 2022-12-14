package com.quocdat.trelloapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quocdat.trelloapp.R
import com.quocdat.trelloapp.activities.TaskListActivity
import com.quocdat.trelloapp.models.Card
import com.quocdat.trelloapp.models.SelectedMembers
import com.quocdat.trelloapp.models.Users
import kotlinx.android.synthetic.main.item_card.view.*

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_card, parent, false))
    }

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model= list[position]

        if (holder is MyViewHolder){

            if (model.labelColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility = View.VISIBLE
                holder.itemView.view_label_color
                    .setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.view_label_color.visibility = View.GONE
            }

            holder.itemView.tv_card_name.text = model.name

            if ((context as TaskListActivity).mAssignedMemberDetailList.size > 0){
                val selectedMemberList: ArrayList<SelectedMembers> = ArrayList()

                for (i in context.mAssignedMemberDetailList.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedMemberDetailList[i].id == j){
                            val selectedMember = SelectedMembers(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image
                            )
                            selectedMemberList.add(selectedMember)
                        }
                    }
                }

                if (selectedMemberList.size > 0){
                    if (selectedMemberList.size == 1 && selectedMemberList[0].id == model.createdBy){
                        holder.itemView.rv_card_selected_member_list.visibility = View.GONE
                    }else{
                        holder.itemView.rv_card_selected_member_list.visibility = View.VISIBLE

                        holder.itemView.rv_card_selected_member_list.layoutManager =
                            GridLayoutManager(context, 4)
                        val adapter = CardMemberListItemAdapter(context, selectedMemberList, false)
                        holder.itemView.rv_card_selected_member_list.adapter = adapter

                        adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener {
                            override fun onClick() {
                                if (onClickListener != null){
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                }else{
                    holder.itemView.rv_card_selected_member_list.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int)
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}