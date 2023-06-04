package com.example.pingpinge.myping

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pingpinge.databinding.ActivityMyPingDetailBinding

class MyPingDetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMyPingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleText = intent.getStringExtra("title")
        val contentText = intent.getStringExtra("content")
        val uri = intent.getStringExtra("uri")

        binding.titleTextView.text = titleText
        binding.contentTextView.text = contentText
        if(uri != null){
            Glide.with(binding.imageView)
                .load(uri)
                .into(binding.imageView)
        }
    }
}