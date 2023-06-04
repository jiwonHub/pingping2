package com.example.pingpinge.community

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pingpinge.DBKey
import com.example.pingpinge.databinding.ActivityCommunityBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding
    private lateinit var communityAdapter: CommunityAdapter
    private lateinit var communityDB : DatabaseReference
    private val communityList = mutableListOf<CommunityDB>()

    private val listener = object : ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val communityModel = snapshot.getValue(CommunityDB::class.java)
            communityModel ?: return

            communityList.add(communityModel)
            communityAdapter.submitList(communityList)
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        communityList.clear()
        communityDB = Firebase.database.reference.child(DBKey.DB_COMMUNITY)
        communityAdapter = CommunityAdapter(onItemClicked = { communityData ->
            val intent = Intent(this, CommunityDetailActivity::class.java)
            intent.putExtra("title", communityData.title)
            intent.putExtra("content", communityData.content)
            startActivity(intent)
        })

        binding.communityRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.communityRecyclerView.adapter = communityAdapter

        binding.communityAddButton.setOnClickListener {
            val intent = Intent(this, CreateCommunityActivty::class.java)
            startActivity(intent)
        }

        communityDB.addChildEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        communityDB.removeEventListener(listener)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        communityAdapter.notifyDataSetChanged()
    }
}