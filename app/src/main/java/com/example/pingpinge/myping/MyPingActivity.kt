package com.example.pingpinge.myping

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pingpinge.DBKey
import com.example.pingpinge.map.PingData
import com.example.pingpinge.databinding.ActivityMyPingBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyPingActivity: AppCompatActivity() {

    private lateinit var myPingAdapter: MyPingAdapter
    private lateinit var pingDB : DatabaseReference

    lateinit var binding: ActivityMyPingBinding
    private val pingList = mutableListOf<PingData>()

    private val listener = object : ChildEventListener {    // listener = firebase에서 저장된 게시글 값들을 가져와서 여기에 저장.
        // 데이터베이스의 특정한 노드에 대한 변경을 수신 대기
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) { // 리스트의 아이템을 검색하거나 추가가 있을 때 수신.
            val noticeBoardModel = snapshot.getValue(PingData::class.java) // 주어진 게시글 형식으로 이루어진 데이터들을 snapshot으로 가져와서 저장.
            noticeBoardModel ?: return

            pingList.add(noticeBoardModel) // noticeBoardModel에 저장된 값을 리스트에 추가.
            myPingAdapter.submitList(pingList) // 아이템 업데이트
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {} // 리스트의 아이템의 변화가 있을때 수신합니다.

        override fun onChildRemoved(snapshot: DataSnapshot) {} //  리스트의 아이템이 삭제되었을때 수신합니다.

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {} // 리스트의 순서가 변경되었을때 수신합니다.

        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pingList.clear()
        pingDB = Firebase.database.reference.child(DBKey.DB_NOTICE_BOARD)
        myPingAdapter = MyPingAdapter(onItemClicked = {pingData ->
            val intent = Intent(this, MyPingDetailActivity::class.java)
            intent.putExtra("title", pingData.title)
            intent.putExtra("content", pingData.content)
            intent.putExtra("uri", pingData.uri)
            startActivity(intent)
        })

        binding.myRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.myRecyclerView.adapter = myPingAdapter

        pingDB.addChildEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        pingDB.removeEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        myPingAdapter.notifyDataSetChanged()
    }
}
