package com.example.goldit.home

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goldit.MainPage.MainPageDirections
import com.example.goldit.R
import com.example.goldit.databinding.FragmentHomeBinding
import org.koin.android.ext.android.inject


class Home() : Fragment() {


    val viewModel: HomeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentHomeBinding = FragmentHomeBinding.inflate(LayoutInflater.from(context))

        binding.viewModel = viewModel

        binding.progressLoaderRates.visibility = VISIBLE
        binding.progressLoader.visibility = VISIBLE

        binding.alertRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.alertRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                outRect.right = resources.getDimension(R.dimen.margin_8).toInt()
                outRect.left = resources.getDimension(R.dimen.margin_8).toInt()

                if (parent.children.indexOf(view) > 0) {
                    outRect.top = resources.getDimension(R.dimen.margin_12).toInt()
                }

            }
        })

        viewModel.liveRateList.observe(viewLifecycleOwner, Observer {
            if (it.size == 2) {
                binding.liveRate22K = it[0]
                binding.liveRate24K = it[1]
                binding.progressLoaderRates.visibility = GONE
                viewModel.loadingInProgress = false
            } else {
                viewModel.updateLiveRates()
                binding.progressLoaderRates.visibility = VISIBLE

            }
        })

        val adapter = AlertAdapter(ActiveAlertsListener {
            this@Home.findNavController().navigate(MainPageDirections.actionMainPageToSetAlert(it))
        })

        binding.alertRecyclerView.adapter = adapter

        viewModel.alertList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                binding.noData.visibility = GONE
                binding.progressLoader.visibility = GONE
            } else {
                binding.noData.visibility = VISIBLE
                binding.progressLoader.visibility = GONE
            }

            adapter.submitList(it)

        })

        viewModel.addNewAlert.observe(viewLifecycleOwner, Observer {
            if (it) {
                this@Home.findNavController()
                    .navigate(MainPageDirections.actionMainPageToSetAlert(0))
                viewModel.navigatedToSetAlert()
            }
        })

        viewModel.userNameClicked.observe(viewLifecycleOwner, Observer {
            if (it) {
                this@Home.findNavController()
                    .navigate(MainPageDirections.actionMainPageToEditProfile())
                viewModel.navigatedToProfileDetails()
            }
        })

        return binding.root

    }

}