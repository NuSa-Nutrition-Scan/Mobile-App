package com.dicoding.picodiploma.nusa_nutritionscan.ui.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.picodiploma.nusa_nutritionscan.API.ApiConfig
import com.dicoding.picodiploma.nusa_nutritionscan.convertUriToFile
import com.dicoding.picodiploma.nusa_nutritionscan.createCustomFile
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentDashboardBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.FoodPredictionResponse
import com.dicoding.picodiploma.nusa_nutritionscan.model.LoginViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebase: FirebaseAuth
    private val loginViewModel: LoginViewModel by viewModels{
        ViewModelFactory(UserPreferenceDatastore.getInstance(requireActivity().dataStore))
    }
    private lateinit var photoPath: String
    private var getFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        firebase = FirebaseAuth.getInstance()

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val user = firebase.currentUser
//        var token = ""
//        user?.getIdToken(true)?.addOnCompleteListener { Task ->
//            if (Task.isSuccessful){
//                token = Task.result.token.toString()
//                Toast.makeText(requireActivity(), "Token didapatkan", Toast.LENGTH_SHORT).show()
//            } else{
//                Toast.makeText(requireActivity(), "Token gagal didapatkan", Toast.LENGTH_SHORT).show()
//            }
//        }
        binding.apply {
            customButtomGallery.setOnClickListener { takePhotoFromGallery() }
            customButtonCamera.setOnClickListener { takePhotoFromCamera() }
            submitButton.setOnClickListener { uploadImage() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun takePhotoFromGallery(){
        val intentToGallery = Intent()
        intentToGallery.action = Intent.ACTION_GET_CONTENT
        intentToGallery.type = "image/*"
        val pickPhoto = Intent.createChooser(intentToGallery, "pilih gambar")
        launcherIntentGallery.launch(pickPhoto)
    }

    private fun takePhotoFromCamera(){
        val intentToCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentToCamera.resolveActivity(requireActivity().packageManager)

        createCustomFile(requireActivity()).also {
            val photoURI: Uri = FileProvider.getUriForFile(requireActivity(), "com.dicoding.picodiploma.mystoryapp", it)
            photoPath = it.absolutePath
            intentToCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intentToCamera)
        }
    }

    private fun uploadImage(){
        var token = ""
        loginViewModel.getUser().observe(requireActivity()){
            token = it.token.toString()
        }
        if (getFile != null){
//            val file = reduceFileImage(getFile as File)
            val file = getFile as File
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val client = ApiConfig.getApiService()
            val call = client.uploadImage(bearer = "Bearer $token", body)
            call.enqueue(object : retrofit2.Callback<FoodPredictionResponse>{
                override fun onFailure(call: retrofit2.Call<FoodPredictionResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Photo gagal dikirim ke URL", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: retrofit2.Call<FoodPredictionResponse>, response: retrofit2.Response<FoodPredictionResponse>) {
                    if (response.isSuccessful){
                        print("ini adalah : ${response.body()}")
                        createMessage(true, response.body()?.data?.finalResult.toString())
                    }
                    else{
                        createMessage(false, null.toString())
                    }
                }
            })
        }
    }

    private fun createMessage(bool: Boolean, makanan: String){
        if (bool){
            Toast.makeText(requireActivity(), "Photo telah dikirim ke URL. makanan adalah ${makanan.toString()}", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireActivity(), "Photo gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            val file =  convertUriToFile(selectedImg, requireActivity())
            getFile = file

            binding?.imageView?.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            val file = File(photoPath)
            file.let { file ->
                getFile = file
                binding?.imageView?.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }
}