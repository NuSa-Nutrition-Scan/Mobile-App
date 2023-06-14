package com.dicoding.picodiploma.nusa_nutritionscan.ui.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.picodiploma.nusa_nutritionscan.Activity.InputInformationActivity
import com.dicoding.picodiploma.nusa_nutritionscan.Activity.LoginActivity
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.dicoding.picodiploma.nusa_nutritionscan.data.dataStore
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentNotificationsBinding
import com.dicoding.picodiploma.nusa_nutritionscan.model.LoginViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.model.ViewModelFactory

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(UserPreferenceDatastore.getInstance(requireActivity().dataStore))
    }
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.nameProfile.text = user?.displayName
//        binding.emailProfile.text = user?.email
//        if (user?.photoUrl != null){
//            Glide.with(this).load(user?.photoUrl).into(binding.imageProfile)
//        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textSignOut.setOnClickListener {
            loginViewModel.getUser().observe(requireActivity()){
//                loginViewModel.refresh(it.refreshToken.toString())
                loginViewModel.logout(it.token.toString())
            }
            val intentToLogin = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intentToLogin)
        }

        binding.textEditProfile.setOnClickListener {
            val intentToEdit = Intent(requireActivity(), InputInformationActivity::class.java)
            startActivity(intentToEdit)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}