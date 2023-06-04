package com.example.pingpinge.community

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pingpinge.databinding.ItemChatBinding

class CommunityDetailAdapter: ListAdapter<CommunityDetailData, CommunityDetailAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommunityDetailAdapter.ViewHolder {
        return ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommunityDetailAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SimpleDateFormat")
        fun bind(chatItem: CommunityDetailData){
            binding.chatTextView.text = chatItem.chat
        }
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<CommunityDetailData>(){
            override fun areItemsTheSame(oldItem: CommunityDetailData, newItem: CommunityDetailData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CommunityDetailData, newItem: CommunityDetailData): Boolean {
                return oldItem == newItem
            }

        }
    }
}
