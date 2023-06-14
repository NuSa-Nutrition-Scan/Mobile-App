package com.dicoding.picodiploma.nusa_nutritionscan.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.dicoding.picodiploma.nusa_nutritionscan.data.dataStore
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.ActivitySplashScreenBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.LoginViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory

class SplashScreen : AppCompatActivity() {
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this){
            if (it.name!!.isEmpty()){
                Handler().postDelayed({
                    val intentToLogin = Intent(this@SplashScreen, LoginActivity::class.java)
                    startActivity(intentToLogin)
                    finish()
                }, 2000L)
            } else{
                Handler().postDelayed({
                    val intentToMain = Intent(this@SplashScreen, MainActivity::class.java)
                    startActivity(intentToMain)
                    finish()
                }, 2000L)
            }
        }

    }
}