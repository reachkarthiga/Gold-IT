package com.example.goldit.setAlert

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.goldit.BuildConfig
import com.example.goldit.PERMISSION_REQUEST_CODE
import com.example.goldit.R
import com.example.goldit.databinding.FragmentSetAlertBinding
import com.example.goldit.repository.carat22
import com.example.goldit.repository.carat24
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notified_alert_detail.*
import kotlinx.android.synthetic.main.fragment_set_alert.*
import org.koin.android.ext.android.inject

const val SAVED_STATE_PRICE = "PRICE"
const val SAVED_STATE_GOLD_TYPE = "TYPE"

class SetAlert : Fragment() {

    val viewModel: SetAlertViewModel by inject()

    private var alertId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentSetAlertBinding.inflate(LayoutInflater.from(context))

        binding.viewModel = viewModel

        val args = SetAlertArgs.fromBundle(requireArguments())

        if (args.alertId > 0) {
            alertId = args.alertId
            binding.alert = viewModel.getAlert(alertId, viewModel.email)
            binding.deleteButton.visibility = VISIBLE
        } else {
            alertId = 0
            viewModel.clearData()
            binding.deleteButton.visibility = GONE

            viewModel.price = savedInstanceState?.getInt(SAVED_STATE_PRICE) ?: 0
            viewModel.caratType = savedInstanceState?.getString(SAVED_STATE_GOLD_TYPE) ?: ""

        }


        val navHostFragment = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.toolBar, navHostFragment)

        binding.toolBar.title = resources.getString(R.string.setAlert)

        viewModel.alertDeleted.observe(viewLifecycleOwner, Observer {
            if (it) {
                this@SetAlert.findNavController()
                    .navigate(SetAlertDirections.actionSetAlertToMainPage())
                viewModel.onDeletedAndNavigatedToMainPage()
            }
        })

        viewModel.saveButton.observe(viewLifecycleOwner, Observer {
            if (it) {

                if (alertId > 0) {
                    viewModel.updateAlert(
                        alertId,
                        viewModel.caratType,
                        viewModel.price,
                        viewModel.email
                    )
                } else {
                    viewModel.saveAlert(viewModel.caratType, viewModel.price, viewModel.email)
                }

                sendMessage("Alert Set for Price Drop at ${viewModel.price}")
                this@SetAlert.findNavController()
                    .navigate(SetAlertDirections.actionSetAlertToMainPage())
                viewModel.navigatedToMainPage()
            }
        })

        viewModel.isPriceError.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(binding.saveButton, "Enter a valid price ", Snackbar.LENGTH_SHORT)
                    .show()
            }
        })

        binding.goldVariations.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.gold_24k -> {
                    binding.gold24k.setTextColor(resources.getColor(R.color.primary))
                    binding.gold24k.setBackgroundResource(R.drawable.primary_color_rectangle_enabled)

                    binding.gold22k.setTextColor(resources.getColor(R.color.black))
                    binding.gold22k.setBackgroundResource(R.drawable.grey_rectangle)

                    viewModel.setGoldType(carat24)
                }

                R.id.gold_22k -> {
                    binding.gold22k.setTextColor(resources.getColor(R.color.primary))
                    binding.gold22k.setBackgroundResource(R.drawable.primary_color_rectangle_enabled)

                    binding.gold24k.setTextColor(resources.getColor(R.color.black))
                    binding.gold24k.setBackgroundResource(R.drawable.grey_rectangle)

                    viewModel.setGoldType(carat22)
                }

                else -> {
                    binding.gold22k.setTextColor(resources.getColor(R.color.black))
                    binding.gold22k.setBackgroundResource(R.drawable.grey_rectangle)
                    binding.gold24k.setTextColor(resources.getColor(R.color.black))
                    binding.gold24k.setBackgroundResource(R.drawable.grey_rectangle)
                }

            }

        }

        viewModel.isGoldTypeError.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(binding.saveButton, "Select the Gold Type", Snackbar.LENGTH_SHORT)
                    .show()
            }
        })

        viewModel.alertCountError.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(
                    binding.saveButton,
                    "You have reached the max alerts allowed for an user. Please delete or modify any existing alert!!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.isAlreadyAchieved.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(
                    binding.saveButton,
                    "Please check the current rates. Its already low!!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.notificationError.observe(viewLifecycleOwner, Observer {
            if (it) {
                val snackBar = Snackbar.make(
                    binding.saveButton,
                    "Notification Permission required to get alerts on price drop!!",
                    Snackbar.LENGTH_INDEFINITE
                )

                snackBar.setAction("Settings", OnClickListener {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }).setActionTextColor(Color.RED)

                snackBar.show()

            }

        })


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkForNotificationPermission()
        } else {
            viewModel.setNotificationError(false)
        }

        return binding.root

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        outState.putString(
            SAVED_STATE_GOLD_TYPE, when {
                (gold_22k.isChecked) -> {
                    carat22
                }
                (gold_24k.isChecked) -> {
                    carat24
                }
                else -> ""
            }
        )

        outState.putInt(SAVED_STATE_PRICE, editText_price_drop.text.toString().toInt())

    }


    private fun sendMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(33)
    private fun checkForNotificationPermission() {
        val isPermissionGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_CODE
            )
        } else {
            viewModel.setNotificationError(false)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                viewModel.setNotificationError(true)
            }

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.setNotificationError(false)
            }

        }

    }


}
