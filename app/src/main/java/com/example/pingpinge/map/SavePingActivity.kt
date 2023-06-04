package com.example.pingpinge.map

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.pingpinge.DBKey.Companion.DB_NOTICE_BOARD
import com.example.pingpinge.R
import com.example.pingpinge.databinding.ActivitySavePingBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class SavePingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavePingBinding

    private var selectedUri: Uri? = null // 선택된 사진
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val storage: FirebaseStorage by lazy { // firebase 스토리지
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy { // firebase 저장 경로
        Firebase.database.reference.child(DB_NOTICE_BOARD)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySavePingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        latitude = intent.getDoubleExtra("latitude" ,0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.back)
            setDisplayShowTitleEnabled(false)

            val customView =
                LayoutInflater.from(this@SavePingActivity).inflate(R.layout.custom_toolbar, null)
            val logoImageView: ImageView = customView.findViewById(R.id.logoImageView)
            val titleTextView: TextView = customView.findViewById(R.id.titleTextView)

            titleTextView.text = "핑 저장하기~!"

            setCustomView(customView)
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        }

        binding.selectButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission( // 사진 갤러리 사용 권한 체크
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE // Manifest에서 갤러리 읽기 쓰기 권한 받았는지 체크
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> { // 권한을 거부했을 경우
                    showPermissionContextPopup()
                }

                else -> { // 체크를 이미 했었다면 바로 갤러리 실행
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        1010
                    )
                }
            }
        }

        // 저장 버튼 클릭 시 데이터베이스에 저장
        binding.saveButton.setOnClickListener {
            val pingTitle = binding.titleEditText.text.toString()
            val pingContent = binding.contentEditText.text.toString()
            val key = System.currentTimeMillis()

            showProgress()
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->
                        uploadNoticeBoard(key, pingTitle, pingContent, uri, latitude, longitude)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드 실패!!", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {
                uploadNoticeBoard(key, pingTitle, pingContent, "", latitude, longitude)
            }
        }
    }

    private fun uploadPhoto( // 사진 업로드 함수
        uri: Uri,                           // 사진 uri
        successHandler: (String) -> Unit,   // 업로드 성공 핸들러
        errorHandler: () -> Unit            // 업로드 실패 핸들러
    ) {
        val fileName = "${System.currentTimeMillis()}.png" // 중복 방지를 위해 현재 시간을 ms으로 변환한 값을 파일명으로 설정.
        storage.reference.child("ping/photo").child(fileName) // firebase storage 저장 경로 설정.
            .putFile(uri) // uri 넣기
            .addOnCompleteListener {
                if (it.isSuccessful) { // uri put이 성공했다면 실제로 uri에 해당하는 사진을 업로드 시작.
                    storage.reference.child("ping/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->      // 성공 시 성공 핸들러에 해당하는 일 시작.
                            successHandler(uri.toString())
                        }.addOnFailureListener {            // 실패 시 실패 핸들러에 해당하는 일 시작.
                            errorHandler()
                        }
                } else {
                    errorHandler() // put에 실패했다면 에러핸들러 실행.
                }
            }
    }

    private fun uploadNoticeBoard( // 게시글 업로드 함수
        key: Long, // 게시글 고유 키값
        title: String,      // 제목
        content: String,    // 내용
        imageUri: String,   // 사진 uri
        lat: Double,
        lng: Double
    ) { // 게시글 업로드
        val model =
            PingData(key, title, content, imageUri, lat, lng) // 게시글 데이터 형식으로 받아온 값들을 model에 저장
        articleDB.push().setValue(model) // 최종적으로 DB에 푸쉬

        hideProgress() // 로딩창 숨기기
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult( // 갤러리 실행 후 결과를 체크하는 함수
        requestCode: Int, // 갤러리 실행 시 보낸 코드
        resultCode: Int,  // 사진 가져오기가 성공 시 갖는 코드
        data: Intent?     // 사진
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) { // 성공하지 못했다면
            return
        }

        when (requestCode) { // 갤러리 실행 시 보낸 코드가 2020일 때
            2020 -> {
                val uri = data?.data // 선택한 사진을 uri에 저장
                if (uri != null) { // 선택한 사진이 null이 아니라면
                    binding.selectImage.setImageURI(uri) // 게시글 만들기 화면에 사진 띄우기
                    selectedUri = uri // 선택한 사진 변수 selectedUri에도 uri저장
                } else { // null이라면
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            else -> { // 2020이 아닐 때
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult( // 권한 체크 시 그 결과를 확인하는 함수
        requestCode: Int,               // 요청할 때 보낸 코드
        permissions: Array<out String>,
        grantResults: IntArray          // 요청에 ok했을 때의 정보를 갖음.
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) { // 요청할 때 보낸 코드가 1010이면
            1010 ->
                if (grantResults.isNotEmpty()) { // 요청 결과에 ok가 있다면
                    startContentProvider() // 갤러리 실행
                } else { // 요청 결과에 ok가 없다면
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() { // 갤러리 띄우기
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionContextPopup() { // 권한 동의x 를 누른 후 띄워지는 팝업
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }

    private fun showProgress() { // 로딩창 o
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() { // 로딩창 x
        binding.progressBar.isVisible = false
    }
}