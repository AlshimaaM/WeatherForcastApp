package com.example.myapplication.view.fragment

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.myapplication.R
import com.example.myapplication.util.ContextUtils.Companion.invalidAddressToast
import com.example.myapplication.util.ContextUtils.Companion.offlineToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapFragment : Fragment() , GoogleMap.OnMapClickListener {
    private lateinit var mMap : GoogleMap
    private lateinit var geocoder: Geocoder
    lateinit var locationSearch: EditText
    lateinit var search: Button
    private val callback = OnMapReadyCallback { googleMap ->
        this.mMap=googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mMap.setOnMapClickListener(this)
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
        locationSearch=view.findViewById(R.id.editText)
        search=view.findViewById(R.id.btn_search)
        search.setOnClickListener({ findCity()})

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        geocoder = Geocoder(context)
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
        try{
            val addresses = geocoder.getFromLocation(p0!!.latitude, p0.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses.get(0)
                val stAddress: String = address.getAddressLine(0)
                mMap.addMarker(MarkerOptions().position(p0).title(stAddress))
                val latLonBundle = bundleOf("latLng" to p0)
                findNavController().navigate(
                        R.id.action_mapsFragment_to_favoriteFragment,
                        latLonBundle
                )
            } else {
                    invalidAddressToast(requireActivity())
            }
        } catch (e: IOException){
            offlineToast(requireActivity())
        }
    }

}
