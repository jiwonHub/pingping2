package com.example.pingpinge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pingpinge.community.CommunityActivity
import com.example.pingpinge.databinding.ActivityMainBinding
import com.example.pingpinge.map.MapActivity
import com.example.pingpinge.myping.MyPingActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val token = task.result
                    Log.d("token", token)
                }
            }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        binding.pingButton.setOnClickListener {
            val intent = Intent(this, MyPingActivity::class.java)
            startActivity(intent)
        }

        binding.communityButton.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }
    }
}