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
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.LoginViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupAction(){
        binding.signupLink.setOnClickListener {
            val intentToRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intentToRegister)
            finish()
        }

        binding.Login.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val pass = binding.passInput.text.toString()

            if (email.isEmpty()){
                Toast.makeText(this, "Email Harus Diisi", Toast.LENGTH_SHORT).show()
            } else if(pass.isEmpty()){
                Toast.makeText(this, "Password Harus Diisi", Toast.LENGTH_SHORT).show()
            } else if(pass.length < 8){
                Toast.makeText(this, "Password Minimal 8 Karakter", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.login(email, pass)
            }
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
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore)))[LoginViewModel::class.java]

        loginViewModel.let { viewModel ->
            viewModel.loginResult.observe(this) { login ->
                viewModel.saveUser(
                    login.data?.name.toString(),
                    login.data?.id.toString(),
                    login.data?.token.toString(),
                    login.data?.refresh_token.toString(),
                    login.data?.sex.toString(),
                    login.data?.weight!!.toInt(),
                    login.data?.eat_per_day!!.toInt(),
                    login.data?.calories_target!!.toInt(),
                    login.data?.has_been_updated == true,
                    login.data?.expires_in.toString(),
                    login.data?.age!!.toInt(),
                    login.data?.email.toString(),
                    login.data?.height!!.toInt()
                )
            }

            viewModel.message.observe(this) { message ->
                if (message == "200") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Good News")
                    builder.setMessage("Login Berhasil :)")
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        loginViewModel.loginResult.observe(this){ login ->
                            if (login.data?.has_been_updated == false){
                                val intentToInput = Intent(this, InputInformationActivity::class.java)
                                startActivity(intentToInput)
                                finish()
                            }
                            else if (login.data?.has_been_updated == true){
                                val intentToMain = Intent(this, MainActivity::class.java)
                                startActivity(intentToMain)
                                finish()
                            }
                        }
                    }, 2000L)
                }
            }

            viewModel.error.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Bad News")
                    builder.setMessage("Email Tidak Valid :(")
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000L)
                }
                if (error == "401") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Bad News")
                    builder.setMessage("User Tidak Ada :(")
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000L)
                }
            }
            viewModel.isLoading.observe(this) { isLoading ->
                progressValue(isLoading)
            }
        }
    }

    private fun progressValue(isLoading: Boolean){
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}