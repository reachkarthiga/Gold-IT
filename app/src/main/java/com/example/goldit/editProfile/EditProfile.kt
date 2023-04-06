package com.example.goldit.editProfile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.goldit.R
import com.example.goldit.databinding.FragmentEditProfileBinding
import com.example.goldit.login.EMAIL
import com.example.goldit.login.FirebaseUserLiveData
import com.example.goldit.login.LOGIN_DETAILS
import com.example.goldit.login.USER_NAME
import com.firebase.ui.auth.AuthUI

class EditProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentEditProfileBinding.inflate(LayoutInflater.from(context))

        val navHostFragment = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.toolBar, navHostFragment)

        binding.toolBar.title = resources.getString(R.string.profile_details)

        val sharedPreferences = requireActivity().getSharedPreferences(LOGIN_DETAILS, Context.MODE_PRIVATE)
        binding.name.text =  sharedPreferences?.getString(USER_NAME, "")
        binding.email.text = sharedPreferences?.getString(EMAIL, "")

        binding.logout.setOnClickListener {
            AuthUI.getInstance().signOut(requireContext())
            updateLocalDataBase("", "")
        }

        FirebaseUserLiveData().observe(viewLifecycleOwner, Observer {
//            if (it == null) {
//                this@EditProfile.findNavController().navigate(R.id.action_editProfile_to_login)
//            }
        })

        binding.about.setOnClickListener {
            this@EditProfile.findNavController().navigate(R.id.action_editProfile_to_aboutApp)
        }

        return binding.root

    }

    private fun updateLocalDataBase(name: String, email: String) {

        val sharedPreferences =
            requireActivity().getSharedPreferences(LOGIN_DETAILS, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString(USER_NAME, name )
        editor.putString(EMAIL, email)
        editor.apply()

    }


}
