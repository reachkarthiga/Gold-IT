package com.example.goldit.MainPage

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.goldit.R
import com.example.goldit.databinding.MainPageBinding
import com.example.goldit.home.Home
import com.example.goldit.notification.ALERT_ID
import com.example.goldit.notifiedAlerts.NotifiedAlerts
import org.koin.android.ext.android.inject

class MainPage : Fragment() {

    val viewModel: MainPageViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = MainPageBinding.inflate(LayoutInflater.from(context))
        binding.bottomNavigation.menu.findItem(R.id.home).setChecked(true)

        val id = arguments?.getLong(ALERT_ID, 0)

        if (id == Long.MIN_VALUE) {
            viewModel.setNotifiedAlertPage()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.bottomNavigation.menu.setGroupDividerEnabled(true)
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                HOME_PAGE -> {
                    viewModel.setHome()
                }

                NOTIFIED_ALERT_PAGE -> {
                    viewModel.setNotifiedAlertPage()
                }
                else -> {
                    viewModel.setHome()
                }
            }
            true

        }

        viewModel.displayPage.observe(viewLifecycleOwner, Observer {
            when (it) {
                HOME_PAGE -> {
                    replaceFragment(Home())
                    binding.bottomNavigation.menu.findItem(R.id.notifiedAlert).setChecked(false)
                    binding.bottomNavigation.menu.findItem(R.id.home).setChecked(true)
                }

                NOTIFIED_ALERT_PAGE -> {
                    replaceFragment(NotifiedAlerts())
                    binding.bottomNavigation.menu.findItem(R.id.home).setChecked(false)
                    binding.bottomNavigation.menu.findItem(R.id.notifiedAlert).setChecked(true)

                }
                else -> {
                    replaceFragment(Home())
                }

            }
        })

        return binding.root

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

}