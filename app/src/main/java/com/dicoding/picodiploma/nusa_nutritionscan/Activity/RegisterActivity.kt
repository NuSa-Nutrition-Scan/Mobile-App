package com.dicoding.picodiploma.nusa_nutritionscan.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.dicoding.picodiploma.nusa_nutritionscan.data.dataStore
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.ActivityRegisterBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.RegisterViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupAction(){
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
                registerViewModel.register(username, email, pass)
            }
        }

        binding.loginLink.setOnClickListener {
            val intentToLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intentToLogin)
        }
    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel(){
        registerViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore)))[RegisterViewModel::class.java]

        registerViewModel.let { viewModels ->
            viewModels.message.observe(this){
                if (it == "201"){
                    val build = AlertDialog.Builder(this)
                    build.setTitle("Good News")
                    build.setMessage("Akun Berhasil Dibuat :)")
                    val alertDialog: AlertDialog = build.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intentToLogin = Intent(this, LoginActivity::class.java)
                        startActivity(intentToLogin)
                    }, 2000L)
                }
            }
            viewModels.error.observe(this){
                if (it == "400"){
                    val build = AlertDialog.Builder(this)
                    build.setTitle("Bad News")
                    build.setMessage("Akun Gagal Dibuat :(")
                    val alertDialog: AlertDialog = build.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000L)
                }
            }
            viewModels.isLoading.observe(this){
                progressValue(it)
            }
        }
    }
    private fun progressValue(isLoading: Boolean){
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}