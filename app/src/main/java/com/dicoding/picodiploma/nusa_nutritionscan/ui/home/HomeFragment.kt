package com.dicoding.picodiploma.nusa_nutritionscan.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dicoding.picodiploma.nusa_nutritionscan.R
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.dicoding.picodiploma.nusa_nutritionscan.data.dataStore
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentHomeBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.MainViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var totalCaloriesToday: Int? = null
//    private val viewModel: MainViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels{
        ViewModelFactory(UserPreferenceDatastore.getInstance(requireActivity().dataStore))
    }

    companion object{
        var CONFIRM = "confirm_food"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        totalCaloriesToday = 0
        binding.totalCalories.text = "$totalCaloriesToday cal"

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHistory: Button = view.findViewById(R.id.history_button)

        mainViewModel.let {viewModel ->
            viewModel.foodDetection.observe(viewLifecycleOwner){
                Toast.makeText(requireActivity(), "calories: ${it.data?.calories.toString()}", Toast.LENGTH_SHORT).show()
                changeCalories(it.data?.calories)
            }
        }

        btnHistory.setOnClickListener {
            val dialogBinding = layoutInflater.inflate(R.layout.fragment_history_pop_up, null)

            val dialogHistoryPopUp = Dialog(requireActivity())
            dialogHistoryPopUp.setContentView(dialogBinding)

            dialogHistoryPopUp.setCancelable(true)
            dialogHistoryPopUp.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialogHistoryPopUp.show()

            val btnCancel: ImageView = dialogBinding.findViewById(R.id.close_icon)
            btnCancel.setOnClickListener {
                dialogHistoryPopUp.dismiss()
            }
        }

        if (arguments != null){
            val confirmationFood = arguments?.getString(CONFIRM)
            if (confirmationFood == "confirm"){
                val dialogBinding = layoutInflater.inflate(R.layout.fragment_success_pop_up, null)

                val dialogConfirmPopUp = Dialog(requireActivity())
                dialogConfirmPopUp.setContentView(dialogBinding)

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
    }

    private fun changeCalories(cal : Int?){
        totalCaloriesToday = totalCaloriesToday?.plus(cal!!)
        binding.totalCalories.text = "${totalCaloriesToday.toString()} cal"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}