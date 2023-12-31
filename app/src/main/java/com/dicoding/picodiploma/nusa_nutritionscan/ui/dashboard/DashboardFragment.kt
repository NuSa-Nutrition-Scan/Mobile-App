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
import com.dicoding.picodiploma.nusa_nutritionscan.model.Data
import com.dicoding.picodiploma.nusa_nutritionscan.model.LoginViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.MainViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory
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
                createDialog(it.data?.name.toString(), it.data)
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
            mainViewModel.foodPrediction(token, file)
        }
    }

    private fun createDialog(food: String, data: Data?){
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

        btnCancel.setOnClickListener {
            dialogHistoryPopUp.dismiss()
        }
        btnConfirm.setOnClickListener {
            mainViewModel.uploadImage(token, getFile as File)
            createDialogFood("confirm", data)
        }
        btnUnConfirm.setOnClickListener {
            createDialogFood("cancel", data)
        }
    }

    private fun createDialogFood(confirmationFood: String, data: Data?){
        if (confirmationFood == "confirm"){
            val dialogBinding = layoutInflater.inflate(R.layout.fragment_success_pop_up, null)

            val dialogConfirmPopUp = Dialog(requireActivity())
            dialogConfirmPopUp.setContentView(dialogBinding)

            val type_food : TextView = dialogBinding.findViewById(R.id.eat_type)
            val food_name : TextView = dialogBinding.findViewById(R.id.food_eaten)
            val add_calories : TextView = dialogBinding.findViewById(R.id.food_cal)
            val protein: TextView = dialogBinding.findViewById(R.id.nutrion_count1)
            val carbo: TextView = dialogBinding.findViewById(R.id.nutrion_count2)
            val lemak: TextView = dialogBinding.findViewById(R.id.nutrion_count3)
            val serat: TextView = dialogBinding.findViewById(R.id.nutrion_count4)
            val air: TextView = dialogBinding.findViewById(R.id.nutrion_count5)

            val value_protein = if(data?.protein == null) 0 else data.protein
            val value_carbo = if(data?.karbohidrat == null) 0 else data.karbohidrat
            val value_lemak = if(data?.lemak == null) 0 else data.lemak
            val value_serat = if(data?.vitamin == null) 0 else data.vitamin
            val value_air = if(data?.mineral == null) 0 else data.mineral

            type_food.text = "\"${data?.name.toString()}\""
            food_name.text = "You Already Eat ${data?.name.toString()}"
            add_calories.text = "+ ${data?.calories.toString()} cal"
            protein.text = "${value_protein.toString()} %"
            carbo.text = "${value_carbo.toString()} %"
            lemak.text = "${value_lemak.toString()} %"
            serat.text = "${value_serat.toString()} %"
            air.text = "${value_air.toString()} %"

            dialogConfirmPopUp.setCancelable(true)
            dialogConfirmPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialogConfirmPopUp.show()

            val btnCancel: ImageView = dialogBinding.findViewById(R.id.close_icon)
            btnCancel.setOnClickListener {
                dialogConfirmPopUp.dismiss()
            }
        }
        else if(confirmationFood == "cancel"){
            val dialogBinding = layoutInflater.inflate(R.layout.fragment_failed_pop_up, null)

            val dialogCancelPopUp = Dialog(requireActivity())
            dialogCancelPopUp.setContentView(dialogBinding)

            dialogCancelPopUp.setCancelable(true)
            dialogCancelPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialogCancelPopUp.show()

            val btnCancel: ImageView = dialogBinding.findViewById(R.id.close_icon)
            btnCancel.setOnClickListener {
                dialogCancelPopUp.dismiss()
            }
        }
        else if(confirmationFood == "expired"){
            val dialogBinding = layoutInflater.inflate(R.layout.fragment_expired_pop_up, null)

            val dialogExpiredPopUp = Dialog(requireActivity())
            dialogExpiredPopUp.setContentView(dialogBinding)

            dialogExpiredPopUp.setCancelable(true)
            dialogExpiredPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialogExpiredPopUp.show()

            val btnCancel: ImageView = dialogBinding.findViewById(R.id.close_icon)
            btnCancel.setOnClickListener {
                dialogExpiredPopUp.dismiss()
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