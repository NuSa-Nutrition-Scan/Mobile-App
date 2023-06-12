package com.dicoding.picodiploma.nusa_nutritionscan.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.dicoding.picodiploma.nusa_nutritionscan.R
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        if (user != null){
            val userName = user.displayName
            Toast.makeText(this, "Welcome $userName", Toast.LENGTH_SHORT).show()

            val intentToMain = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intentToMain)
            finish()
        }

        supportActionBar?.hide()

        binding.signupLink.setOnClickListener {
            val intentToRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intentToRegister)
            finish()
        }

        binding.Login.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val pass = binding.passInput.text.toString()

            if (email.isEmpty()){

            } else if(pass.isEmpty()){

            } else if(pass.length < 8){

            } else {
                signInWithEmailAndPassword(email, pass)
            }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user = firebaseAuth.currentUser
                val userName = user?.displayName
                Toast.makeText(this, "Welcome $userName", Toast.LENGTH_SHORT).show()

                val intentToMain = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intentToMain)
                finish()
            }
            else{
                val exception = task.exception
                Toast.makeText(this, "Failed: $exception", Toast.LENGTH_SHORT).show()
            }
        }
    }
}