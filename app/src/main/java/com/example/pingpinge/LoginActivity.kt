package com.example.pingpinge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.pingpinge.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginOutButton.setOnClickListener {
            binding.let {binding ->
                val email = binding.idEditText.text.toString()
                val password = binding.pwEditText.text.toString()

                if (auth.currentUser == null){
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this){task->
                            if(task.isSuccessful){
                                successSignIn()
                            } else {
                                Toast.makeText(this, "로그인에 실패하셨습니다ㅜㅜ", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    auth.signOut()
                    binding.idEditText.text.clear()
                    binding.idEditText.isEnabled = true
                    binding.pwEditText.text.clear()
                    binding.pwEditText.isEnabled = true

                    binding.loginOutButton.text = "로그인"
                    binding.loginOutButton.isEnabled = true
                    binding.joinButton.isEnabled = false
                }
            }
        }

        binding.joinButton.setOnClickListener {
            binding.let { binding ->
                val email = binding.idEditText.text.toString()
                val password = binding.pwEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful){
                            Toast.makeText(this, "회원가입에 성공했습니다!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "회원가입에 실패했어요ㅜ 이미 가입한 이메일일 수 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.idEditText.addTextChangedListener{
            binding.let {binding ->
                val enable = binding.idEditText.text.isNotEmpty() && binding.pwEditText.text.isNotEmpty()
                binding.joinButton.isEnabled = enable
                binding.loginOutButton.isEnabled = enable
            }
        }

        binding.pwEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.idEditText.text.isNotEmpty() && binding.pwEditText.text.isNotEmpty()
                binding.joinButton.isEnabled = enable
                binding.loginOutButton.isEnabled = enable
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.signOut()

        if(auth.currentUser == null){
            binding?.let { binding ->
                binding.idEditText.text.clear()
                binding.idEditText.isEnabled = true
                binding.pwEditText.text.clear()
                binding.pwEditText.isEnabled = true
                binding.loginOutButton.text = "로그인"
                binding.loginOutButton.isEnabled = false
                binding.joinButton.isEnabled = false
            }
        } else {
            binding?.let { binding ->
                binding.idEditText.setText(auth.currentUser!!.email)
                binding.idEditText.isEnabled = false
                binding.pwEditText.setText("*********")
                binding.pwEditText.isEnabled = false
                binding.loginOutButton.text = "로그아웃"
                binding.loginOutButton.isEnabled = true
                binding.joinButton.isEnabled = false
            }
        }
    }

    private fun successSignIn(){
        if(auth.currentUser == null){
            Toast.makeText(this, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        binding?.idEditText?.isEnabled = false
        binding?.pwEditText?.isEnabled = false
        binding?.joinButton?.isEnabled = false
        binding?.loginOutButton?.text = "로그아웃"
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}