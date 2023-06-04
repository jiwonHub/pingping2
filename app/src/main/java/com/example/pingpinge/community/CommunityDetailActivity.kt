package com.example.pingpinge.community

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pingpinge.DBKey.Companion.DB_CHAT
import com.example.pingpinge.databinding.ActivityCommunityDetailBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class CommunityDetailActivity: AppCompatActivity() {

    private val chatList= mutableListOf<CommunityDetailData>()
    private val adapter = CommunityDetailAdapter()
    private var chatDB : DatabaseReference? = null

    private lateinit var binding: ActivityCommunityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val key = System.currentTimeMillis()

        binding.title.text = title.toString()
        binding.content.text = content.toString()

        chatDB = Firebase.database.reference.child(DB_CHAT).child("$key")
        chatDB?.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(CommunityDetailData::class.java)
                chatItem ?: return

                chatList.add(chatItem) // 가져온 채팅들을 리스트(chatList)에 저장.
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        binding.sendButton.setOnClickListener {
            val chatItem = CommunityDetailData(
                chat = binding.chatEditText.text.toString()
            )
            binding.chatEditText.text = null

            chatDB?.push()?.setValue(chatItem)
        }

    }
}