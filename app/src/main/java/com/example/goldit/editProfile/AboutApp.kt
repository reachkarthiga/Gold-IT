package com.example.goldit.editProfile

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.goldit.R
import com.example.goldit.databinding.FragmentAboutAppBinding

class AboutApp: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAboutAppBinding.inflate(layoutInflater)

        Glide.with(requireContext())
            .load("https://jamamarketing.s3.ap-south-1.amazonaws.com/Jama+Mktng+-+Sachin/Should+Gold+Be+Part+Of+Your+InvestmentGold+Investment.jpg")
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_image_not_supported_24).circleCrop()
            .into(binding.imageView)

        return binding.root
    }


}