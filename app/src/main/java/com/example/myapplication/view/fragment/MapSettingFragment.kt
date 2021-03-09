package com.example.myapplication.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.ContextUtils
import com.example.myapplication.util.ContextUtils.Companion.invalidAddressToast
import com.example.myapplication.util.ContextUtils.Companion.offlineToast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapSettingFragment : Fragment() , GoogleMap.OnMapClickListener {
    private lateinit var mMap : GoogleMap
    lateinit var locationSearch: EditText
    lateinit var search: Button
    private lateinit var geocoder: Geocoder
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private val callback = OnMapReadyCallback { googleMap ->
        this.mMap=googleMap
        mMap.setOnMapClickListener(this)
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        val fMap:MapFragment=MapFragment()
        locationSearch=view.findViewById(R.id.editText)
        search=view.findViewById(R.id.btn_search)
        search.setOnClickListener({ findCity()})

        geocoder = Geocoder(context)
    }
    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
    fun findCity() {
        lateinit var location: String
        location = locationSearch.text.toString()
        var addressList: List<Address>? = null
        if (location == null || location == "") {
            Toast.makeText(requireActivity(),"provide location",Toast.LENGTH_SHORT).show()
        }
        else{
            val geoCoder = Geocoder(context)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
                if (addressList?.size!=0){
                    val address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
                    //  mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    onMapClick(latLng)
                }else{
                    invalidAddressToast(requireActivity())
                }
            } catch (e: IOException) {
                offlineToast(requireActivity())
            }
        }
    }

    override fun onMapClick(p0: LatLng?) {
        init()
        try{
            val addresses = geocoder.getFromLocation(p0!!.latitude, p0.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses.get(0)
                val stAddress: String = address.getAddressLine(0)
                mMap.addMarker(MarkerOptions().position(p0).title(stAddress))
                val latLonBundle = bundleOf("latLng" to p0)
                editor.putString("lat",p0.latitude.toString())
                editor.putString("lon",p0.longitude.toString())
                editor.apply()
                editor.commit()
                Setting.mapLatitude = p0.latitude.toString()
                Setting.mapLongitude =p0.longitude.toString()
                findNavController().navigate(
                    R.id.action_mapSettingFragment_to_homeFragment
                )
            } else {
                invalidAddressToast(requireActivity())

            }
        } catch (e: IOException){
            offlineToast(requireActivity())
        }
    }
}
