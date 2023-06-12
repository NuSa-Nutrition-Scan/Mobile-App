package com.dicoding.picodiploma.nusa_nutritionscan.ui.dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.loader.content.AsyncTaskLoader
import com.dicoding.picodiploma.nusa_nutritionscan.*
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File

val client = OkHttpClient()

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebase: FirebaseAuth

    private lateinit var photoPath: String
    private var getFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        firebase = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val user = firebase.currentUser
        if (getFile != null){
//            val file = reduceFileImage(getFile as File)
            val file = getFile
            val requestImageFile = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val image = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file?.name,
                    file!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .build()
//            val image = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

            val request = Request.Builder()
                .url("https://storage.googleapis.com/nusa-bucket/${user?.uid}/nutrition/photo")
                .post(image)
                .build()

            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Toast.makeText(requireActivity(), "Photo telah dikirim ke URL", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    Looper.prepare()
                    if (response.isSuccessful){
                        Toast.makeText(requireActivity(), "Photo telah dikirim ke URL", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(requireActivity(), "Photo gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            val data = hashMapOf(
                "img_url" to request.url,
                "user_id" to user?.uid
            )

            firestore.collection("photo").add(data)
                .addOnSuccessListener {
//                    Toast.makeText(requireActivity(), "Photo telah dikirim", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
//                    Toast.makeText(requireActivity(), "Photo telah gagal dikirim", Toast.LENGTH_SHORT).show()
                }
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