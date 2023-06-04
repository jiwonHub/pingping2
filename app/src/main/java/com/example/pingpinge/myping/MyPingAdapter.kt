package com.example.pingpinge.myping


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pingpinge.map.PingData
import com.example.pingpinge.databinding.ItemMyPingBinding

class MyPingAdapter(private val onItemClicked: (PingData) -> Unit) : ListAdapter<PingData, MyPingAdapter.ViewHolder>(DiffUtil){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyPingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPingAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(private val binding: ItemMyPingBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: PingData){
            binding.pingTitle.text = item.title
            binding.pingContent.text = item.content

            binding.pingTitle.setOnClickListener {
                onItemClicked(item)
            }
            binding.pingContent.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    companion object{
        val DiffUtil = object : DiffUtil.ItemCallback<PingData>(){ // 기존의 데이터 리스트와 교체할 데이터 리스트를 비교해서 실질적으로 업데이트가 필요한 아이템들을 추려낸다.
            override fun areItemsTheSame(oldItem: PingData, newItem: PingData): Boolean {
                return oldItem == newItem
            }                             // 두 아이템이 동일한 아이템인지 체크한다. 예를들어 item 이 자신만의 고유한 id 같은걸 가지고 있다면 그걸 기준으로 삼으면 된다.

            override fun areContentsTheSame(oldItem: PingData, newItem: PingData): Boolean {
                return oldItem == newItem // 두 아이템이 동일한 내용물을 가지고 있는지 체크한다. 이 메서드는 areItemsTheSame()가 true 일 때만 호출된다.
            }

        }
    }
}