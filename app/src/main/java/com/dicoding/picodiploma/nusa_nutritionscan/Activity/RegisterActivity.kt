package com.dicoding.picodiploma.nusa_nutritionscan.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        supportActionBar?.hide()

        binding.signUpButton.setOnClickListener {
            val username = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val pass = binding.passInput.text.toString()

            if (email.isEmpty()){
                Toast.makeText(this, "Email Harus Diisi", Toast.LENGTH_SHORT).show()
            } else if (username.isEmpty()){
                Toast.makeText(this, "Username Harus Diisi", Toast.LENGTH_SHORT).show()
            } else if (pass.isEmpty()){
                Toast.makeText(this, "Password Harus Diisi", Toast.LENGTH_SHORT).show()
            } else if (pass.length < 8){
                Toast.makeText(this, "Password Minimal 8 Karakter", Toast.LENGTH_SHORT).show()
            } else{
                createUserWithEmailAndPassword(username, email, pass)
            }
        }

        binding.loginLink.setOnClickListener {
            val intentToLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intentToLogin)
        }
    }

    private fun createUserWithEmailAndPassword(username: String, email: String, password: String){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user = firebaseAuth.currentUser
                val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(username).build()

                user?.updateProfile(profileUpdate)?.addOnCompleteListener { profileTask ->
                    if (profileTask.isSuccessful){
                        val displayName = user.displayName
                        val email = user.email
                        Toast.makeText(this, "User created: $displayName, $email", Toast.LENGTH_SHORT).show()

                        val intentToLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intentToLogin)
                    } else {
                        val exception = profileTask.exception
                        Toast.makeText(this, "Failed: $exception", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val exception = task.exception
                Toast.makeText(this, "Failed: $exception", Toast.LENGTH_SHORT).show()
            }
        }
    }
}