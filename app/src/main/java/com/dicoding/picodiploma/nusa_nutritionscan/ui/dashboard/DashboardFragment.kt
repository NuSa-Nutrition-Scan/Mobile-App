package com.dicoding.picodiploma.nusa_nutritionscan.ui.dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.nusa_nutritionscan.R
import com.dicoding.picodiploma.nusa_nutritionscan.convertUriToFile
import com.dicoding.picodiploma.nusa_nutritionscan.createCustomFile
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentDashboardBinding
import java.io.File

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoPath: String
    private var getFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            customButtomGallery.setOnClickListener { takePhotoFromGallery() }
            customButtonCamera.setOnClickListener { takePhotoFromCamera() }
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