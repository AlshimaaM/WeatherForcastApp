package com.example.myapplication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.viewmodel.AlertsViewModel

class AlertsFragment : Fragment() {

    private lateinit var notificationsViewModel: AlertsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProvider(this).get(AlertsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_alerts, container, false)
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }
}