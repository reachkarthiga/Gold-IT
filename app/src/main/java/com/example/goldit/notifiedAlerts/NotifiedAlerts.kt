package com.example.goldit.notifiedAlerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goldit.R
import com.example.goldit.databinding.FragmentNotifiedAlertsBinding
import com.example.goldit.notification.ALERT_ID
import org.koin.android.ext.android.inject


class NotifiedAlerts : Fragment() {

    val viewModel: NotifiedAlertViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNotifiedAlertsBinding.inflate(LayoutInflater.from(context))

        binding.progressLoader.visibility = VISIBLE

        val adapter = NotificationAdapter(NotifiedAlertListener {
            detailPageTransition(it)
            viewModel.detailPageId = it
            viewModel._page.value = DETAIL_PAGE
        })

        viewModel.notifiedAlertList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
                binding.noData.visibility = GONE
                binding.progressLoader.visibility = GONE
            } else {
                binding.noData.visibility = VISIBLE
                binding.progressLoader.visibility = GONE
            }
        })

        binding.notificationRecyclerView.adapter = adapter
        binding.notificationRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root

    }

    private fun detailPageTransition(it:Long) {

        val bundle = Bundle()
        bundle.putLong(ALERT_ID, it)


        this@NotifiedAlerts.findNavController()
            .navigate(
                R.id.action_mainPage_to_notificationDetail, bundle
            )

    }

}
