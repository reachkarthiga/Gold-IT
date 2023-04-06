package com.example.goldit.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.example.goldit.R
import com.example.goldit.databinding.FragmentLoginBinding
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


private const val SIGN_IN = 1
const val LOGIN_DETAILS = "LoginAccount"
const val USER_NAME = "userName"
const val EMAIL = "emailOrMobile"

class Login : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.fragment_login)
            .setEmailButtonId(R.id.email_button).setGoogleButtonId(R.id.google_button).build()

        FirebaseUserLiveData().observe(viewLifecycleOwner, Observer {

            if (it != null ) {
                updateLocalDataBase(FirebaseAuth.getInstance().currentUser?.displayName.toString(), FirebaseAuth.getInstance().currentUser?.email.toString() )
                 this@Login.findNavController().navigate(LoginDirections.actionLoginToMainPage())
                requireActivity().getFragmentManager().popBackStack();
            }

            if (it == null) {

                updateLocalDataBase("", "")

                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(customLayout).setTheme(R.style.loginBackground)
                        .setAvailableProviders(providers)
                        .build(),
                    SIGN_IN
                )
            }
        })

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

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == SIGN_IN) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            Log.i("Login", "${FirebaseAuth.getInstance().currentUser?.displayName.toString()}")
        }
    }

}


}
