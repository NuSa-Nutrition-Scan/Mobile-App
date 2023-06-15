package com.dicoding.picodiploma.nusa_nutritionscan.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.dicoding.picodiploma.nusa_nutritionscan.data.dataStore
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.ActivityInputInformationBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.LoginViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory

class InputInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputInformationBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userName: String
    private lateinit var refresh_token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore)))[LoginViewModel::class.java]

        var token = ""
        loginViewModel.getUser().observe(this){
            binding.nameProfile.text = it.name
            binding.emailProfile.text = it.email
            token = it.token.toString()
            userName = it.name.toString()
            refresh_token = it.refresh_token.toString()
        }

        loginViewModel.message.observe(this){
            if (it == "200"){
                val intentToMain = Intent(this@InputInformationActivity, MainActivity::class.java)
                startActivity(intentToMain)
            }
        }

        binding.submitButton.setOnClickListener {
            submitData(token)
        }
    }

    private fun submitData(token: String) {
        val jenis_kelamin = binding.JKED.text.toString()
        val umur = binding.umurED.text.toString()
        val berat_badan = binding.BBED.text.toString()
        val tinggi_bandan = binding.TBED.text.toString()
        val target_kalori = binding.caloriED.text.toString()

        if (jenis_kelamin.isEmpty()) {
            Toast.makeText(this, "Jenis Kelamin tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (umur.isEmpty()) {
            Toast.makeText(this, "Umur tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (berat_badan.isEmpty()) {
            Toast.makeText(this, "Berat Badan tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (tinggi_bandan.isEmpty()) {
            Toast.makeText(this, "Tinggi Badan tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (target_kalori.isEmpty()) {
            Toast.makeText(this, "Target Kalori per hari tidak boleh kosong", Toast.LENGTH_SHORT)
                .show()
        } else {
            var JK = jenis_kelamin.lowercase()
            if (JK.contains("laki")){
                JK = "M"
            } else{
                JK = "F"
            }
            loginViewModel.updateProfile(userName,refresh_token,token, JK.toString(), umur, berat_badan,tinggi_bandan,target_kalori)
        }
    }
}