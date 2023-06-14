package com.dicoding.picodiploma.nusa_nutritionscan.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.ActivityInputInformationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class InputInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputInformationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        firestore = FirebaseFirestore.getInstance()

        supportActionBar?.hide()
        binding.nameProfile.text = user?.displayName
        binding.emailProfile.text = user?.email
        if (user?.photoUrl != null) {
            Glide.with(this).load(user.photoUrl).into(binding.imageProfile)
        }

        setDataUser(user)
        binding.submitButton.setOnClickListener {
            submitData(user)
            val intentToMain = Intent(this@InputInformationActivity, MainActivity::class.java)
            startActivity(intentToMain)
        }
    }

    private fun setDataUser(user: FirebaseUser?) {
        firestore.collection("user_detail")
            .whereEqualTo("user_id", user?.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                for (documentSnapshot in it) {
                    val user_detail = documentSnapshot.data
                    val JK = user_detail?.get("jenis_kelamin") as String?
                    if (JK == "M"){
                        binding.JKED.setText("Laki-Laki")
                    } else{
                        binding.JKED.setText("Perempuan")
                    }
                    binding.umurED.setText(user_detail?.get("umur") as String?)
                    binding.BBED.setText(user_detail?.get("berat_badan") as String?)
                    binding.TBED.setText(user_detail?.get("tinggi_badan") as String?)
                    binding.caloriED.setText(user_detail?.get("target_kalori") as String?)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "data gagal diambil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun submitData(user: FirebaseUser?) {
        val jenis_kelamin = binding.JKED.text.toString()
        val umur = binding.umurED.text.toString()
        val berat_badan = binding.BBED.text.toString()
        val tinggi_bandan = binding.TBED.text.toString()
        val target_kalori = binding.caloriED.text.toString()
        val date = SimpleDateFormat("dd-MMM-yyyy-HH:mm:ss", Locale.US).format(System.currentTimeMillis())

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
            val data = hashMapOf(
                "create_at" to date.toString(),
                "user_id" to user?.uid,
                "jenis_kelamin" to JK.toString(),
                "umur" to umur,
                "berat_badan" to berat_badan,
                "tinggi_badan" to tinggi_bandan,
                "target_kalori" to target_kalori
            )

//            firestore.collection("user_detail")
//                .add(data)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "data telah diubah", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "data gagal diubah", Toast.LENGTH_SHORT).show()
//                }
        }
    }
}