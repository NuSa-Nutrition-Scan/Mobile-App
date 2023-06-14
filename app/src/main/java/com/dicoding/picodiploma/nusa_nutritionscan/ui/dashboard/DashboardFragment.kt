package com.dicoding.picodiploma.nusa_nutritionscan.ui.dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dicoding.picodiploma.nusa_nutritionscan.R
import com.dicoding.picodiploma.nusa_nutritionscan.convertUriToFile
import com.dicoding.picodiploma.nusa_nutritionscan.createCustomFile
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.dicoding.picodiploma.nusa_nutritionscan.data.dataStore
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentDashboardBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.LoginViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.MainViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory
import com.dicoding.picodiploma.nusa_nutritionscan.ui.home.HomeFragment
import java.io.File

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels{
        ViewModelFactory(UserPreferenceDatastore.getInstance(requireActivity().dataStore))
    }
    private val mainViewModel: MainViewModel by activityViewModels{
        ViewModelFactory(UserPreferenceDatastore.getInstance(requireActivity().dataStore))
    }
    private lateinit var photoPath: String
    private lateinit var token: String
    private var getFile: File? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()

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

    private fun setupViewModel(){
        loginViewModel.getUser().observe(requireActivity()){
            token = it.token.toString()
        }

        mainViewModel.let { viewModel ->
            viewModel.isLoading.observe(viewLifecycleOwner){
                progressValue(it)
            }

            viewModel.foodDetection.observe(viewLifecycleOwner){
                createMessage(it.data?.name.toString())
                createDialog(it.data?.name.toString())
            }
        }
    }

    private fun progressValue(isLoading: Boolean){
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun createMessage(food: String){
        Toast.makeText(requireActivity(), "makanan yang dideteksi ${food.toString()}", Toast.LENGTH_SHORT).show()
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
        if (getFile != null){
            val file = getFile as File
            mainViewModel.uploadImage(token, file)
        }
    }

    private fun createDialog(food: String){
        val dialogBinding = layoutInflater.inflate(R.layout.fragment_confirm_food, null)
        val tvFood =  dialogBinding.findViewById<TextView>(R.id.food_detect)
        tvFood.text = food

        val dialogHistoryPopUp = Dialog(requireActivity())
        dialogHistoryPopUp.setContentView(dialogBinding)

        dialogHistoryPopUp.setCancelable(true)
        dialogHistoryPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialogHistoryPopUp.show()

        val btnCancel: ImageView = dialogBinding.findViewById(R.id.close_icon)
        val btnConfirm: Button = dialogBinding.findViewById(R.id.btnConfirm)
        val btnUnConfirm: TextView = dialogBinding.findViewById(R.id.txtCancel)

        val homeFragment = HomeFragment()
        val bundle = Bundle()

        val fragmentManager = parentFragmentManager
        btnCancel.setOnClickListener {
            dialogHistoryPopUp.dismiss()
        }
        btnConfirm.setOnClickListener {
//            bundle.putString(HomeFragment.CONFIRM, "confirm")
//            homeFragment.arguments = bundle
//            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, homeFragment , HomeFragment::class.java.simpleName).addToBackStack(null).commit()
        }
        btnUnConfirm.setOnClickListener {
//            bundle.putString(HomeFragment.CONFIRM, "cancel")
//            homeFragment.arguments = bundle
//            fragmentManager.beginTransaction().setReorderingAllowed(true).replace(R.id.nav_host_fragment_activity_main, homeFragment , HomeFragment::class.java.simpleName).addToBackStack(null).commit()
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