package com.example.goldit.notifiedAlerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.goldit.R
import com.example.goldit.databinding.FragmentNotifiedAlertDetailBinding
import com.example.goldit.notification.ALERT_ID
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat

class NotifiedAlertDetail : Fragment() {

    val viewModel: NotifiedAlertViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = FragmentNotifiedAlertDetailBinding.inflate(inflater)
        val id = arguments?.getLong(ALERT_ID) ?: 0

        val navHostFragment = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.toolBar, navHostFragment)

        binding.toolBar.title = resources.getString(R.string.notified_alert_detail)

        val alert = viewModel.getAlertDetails(id, viewModel.email)
        binding.alert = alert
        binding.liveRate = viewModel.getLiveRate(alert?.goldType ?: "")
        binding.viewModel = viewModel

        val sdf = SimpleDateFormat("dd-MM-yyyy")

        if (sdf.format(alert?.achievedDateTime ?: 0) == sdf.format(System.currentTimeMillis())) {
            binding.liveRate = viewModel.getLiveRate(alert?.goldType ?: "")
            binding.livePrice.visibility = VISIBLE
            binding.livePriceText.visibility = VISIBLE
            binding.achievedTodayText.visibility = VISIBLE
            binding.enableButton.visibility = GONE
        } else {
            binding.livePrice.visibility = GONE
            binding.livePriceText.visibility = GONE
            binding.achievedTodayText.visibility = GONE
            binding.enableButton.visibility = VISIBLE
        }

        viewModel.moveToListPage.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(
                    this@NotifiedAlertDetail.requireContext(),
                    "Alert enabled again",
                    Toast.LENGTH_LONG
                ).show()
                this.findNavController().navigate(R.id.mainPage)
                viewModel.movedToListPage()
            }
        })


        viewModel.alertCountError.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(
                    binding.enableButton,
                    "You have reached the max alerts allowed for an user. Please delete or modify any existing alert!!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })

        binding.lifecycleOwner = this

        return binding.root

    }
}