package com.example.pingpinge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pingpinge.community.CommunityActivity
import com.example.pingpinge.databinding.ActivityMainBinding
import com.example.pingpinge.map.MapActivity
import com.example.pingpinge.myping.MyPingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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