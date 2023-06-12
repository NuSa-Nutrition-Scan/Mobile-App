package com.dicoding.picodiploma.nusa_nutritionscan.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.nusa_nutritionscan.Activity.InputInformationActivity
import com.dicoding.picodiploma.nusa_nutritionscan.Activity.LoginActivity
import com.dicoding.picodiploma.nusa_nutritionscan.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.FirebaseAuth

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        binding.nameProfile.text = user?.displayName
        binding.emailProfile.text = user?.email
        if (user?.photoUrl != null){
            Glide.with(this).load(user?.photoUrl).into(binding.imageProfile)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textSignOut.setOnClickListener {
            firebaseAuth.signOut()
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