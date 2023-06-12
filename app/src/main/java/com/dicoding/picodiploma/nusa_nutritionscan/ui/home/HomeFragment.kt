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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.nusa_nutritionscan.R
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentHomeBinding
import com.dicoding.picodiploma.nusa_nutritionscan.ui.home.DialogFragment.HistoryPopUpFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHistory: Button = view.findViewById(R.id.history_button)
        val totalCaloriesToday = 0
        binding.totalCalories.text = "$totalCaloriesToday cal"

        btnHistory.setOnClickListener {
//            val historyPopUp = HistoryPopUpFragment()
//            historyPopUp.show((activity as AppCompatActivity).supportFragmentManager, "historyPopUp")

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}