package com.example.pingpinge.myping

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pingpinge.databinding.ActivityMyPingDetailBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MyPingDetailActivity: AppCompatActivity(){

    private lateinit var binding: ActivityMyPingDetailBinding
    private lateinit var calendar: Calendar
    private var selectedDateTime: LocalDateTime? = null

    @RequiresApi(Build.VERSION_CODES.O)
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

        binding.datePickButton.setOnClickListener {
            val currentTime = LocalDateTime.now()

            DatePickerDialog(
                this,
                {_, year, month, dayOfMonth ->
                    TimePickerDialog(
                        this,
                        {_,hourOfDay, minute->
                            selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                            val calendar = Calendar.getInstance()
                            calendar.set(selectedDateTime!!.year, selectedDateTime!!.monthValue - 1, selectedDateTime!!.dayOfMonth, selectedDateTime!!.hour, selectedDateTime!!.minute)
                            startAlarm(calendar)
                            Toast.makeText(this, "${selectedDateTime}에 알림 설정 완료!", Toast.LENGTH_SHORT).show()
                        },
                        currentTime.hour,
                        currentTime.minute,
                        true
                    ).show()
                },
                currentTime.year,
                currentTime.monthValue - 1,
                currentTime.dayOfMonth
            ).show()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startAlarm(c: Calendar){
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlertReceiver::class.java)
        var titleText = binding.titleTextView.text.toString()
        intent.putExtra("title", titleText)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }
}
