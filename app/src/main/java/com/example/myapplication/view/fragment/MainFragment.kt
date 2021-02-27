package com.example.myapplication.view.fragment

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.provider.Setting

class MainFragment() : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.hoursRecyclerview.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.daysRecyclerview.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        settings()

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
            return binding.root

    }

        override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
        ) {
            when (requestCode) {
                1 -> {
                    if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        if ((ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) ===
                                    PackageManager.PERMISSION_GRANTED)
                        ) {
                            Toast.makeText(
                                requireActivity(), "permission garanted", Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireActivity(), "permission deny", Toast.LENGTH_LONG
                        ).show()
                    }
                    return
                }

            }
        }

    private fun settings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val unitSystem = sharedPreferences.getString("UNIT_SYSTEM", "")
        val languageSystem = sharedPreferences.getString("LANGUAGE_SYSTEM", "")
        val location1 = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)
        if (unitSystem != null) {
            Setting.unitSystem = unitSystem
        }
        if (languageSystem != null) {
            Setting.languageSystem = languageSystem
        }
        if (location1 != null) {
            Setting.deviceLocation = location1
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment()
                .apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}