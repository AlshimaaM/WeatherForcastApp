package com.example.myapplication.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapter.DayAdapter
import com.example.myapplication.adapter.HoursAdapter
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.local.database.entity.WeatherEntity
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.remote.RetrofitInstance.getImage
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.Daily
import com.example.myapplication.model.Hourly
import com.example.myapplication.model.Model
import com.example.myapplication.provider.Setting
import com.example.myapplication.viewmodel.WeatherViewModel
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment :  Fragment()  {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: WeatherViewModel
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var dayAdapter: DayAdapter
    val PERMISSION_ID = 42
    private lateinit var icon: String
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.hoursRecyclerview.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.daysRecyclerview.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        hoursAdapter = HoursAdapter()
        dayAdapter = DayAdapter()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        homeViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        settings()
        if (sharedPreferences.getBoolean("USE_DEVICE_LOCATION", true)) {
            getLastLocation()
        }else{
                homeViewModel.getPlace(location = Setting.customLocations)
                homeViewModel.locationMutableLiveData.observe(viewLifecycleOwner, Observer {
                    homeViewModel.fetchweather(it.lat.toString(), it.lon.toString())
                })
        }

        viewWeather(Setting.latitude, Setting.longitude)

        return binding.root
        }
   /* fun viewWeather(latitude: String, longitude: String){
        homeViewModel.fetchweather(latitude, longitude).observe(viewLifecycleOwner, {
            binding.pressure.text = it.current.pressure.toString()
            binding.dateHome.text = "${RetrofitInstance.dateNow}"
            binding.tempreture.text = it.current.temp.toString()
            binding.humidity.text = it.current.humidity.toString() + "%"
            binding.cloud.text = it.current.clouds.toString()
            binding.cityName.text = it.timezone
            binding.wind.text = it.current.wind_speed.toString()
            binding.discription.text = it.current.weather[0].description
            binding.maxTep.text = it.daily[0].temp.max.toString()
            binding.minTep.text = it.daily[0].temp.min.toString()

            icon = it.current.weather[0].icon
            context?.let {
                Glide.with(it).load(getImage(icon)).into(binding.iconToday)
            }
            var listHours: List<Hourly> = it.hourly
            var listDaily: List<Daily> = it.daily
            hoursAdapter.setData(listHours, requireContext())
            binding.hoursRecyclerview.adapter = hoursAdapter
            dayAdapter.fetchData(listDaily, requireContext())
            binding.daysRecyclerview.adapter = dayAdapter
        })

    }*/

    @SuppressLint("MissingPermission")
     fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Setting.latitude = String.format("%.6f", location?.latitude)
                        Setting.longitude = String.format("%.6f", location?.longitude)
                       // viewWeather(Setting.latitude, Setting.longitude)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getActivity()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            /*   var location = mFusedLocationClient(context)
                Setting.latitude = String.format("%.6f", location?.latitude)
                Setting.longitude = String.format("%.6f", location?.longitude)
                viewWeather(Setting.latitude!!, Setting.longitude!!)*/
                getLastLocation()
            }
        }
    }
    fun writeIntoDatabase(model: Model): WeatherEntity {
        val hoursListDB = arrayListOf<HoursEntity>()
        for (hourItem in model.hourly) {
            hoursListDB.add(
                HoursEntity(
                    hourItem.dt.toInt(),
                    hourItem.temp,
                    hourItem.weather[0].icon
                )
            )
        }
        val daysListDB = arrayListOf<DaysEntity>()
        for (dayItem in model.daily) {
            daysListDB.add(
                DaysEntity(
                    dayItem.dt,
                    dayItem.temp.min,
                    dayItem.temp.max,
                    dayItem.weather[0].icon,
                    dayItem.sunrise,
                    dayItem.weather[0].description
                )
            )
        }
        val weatherDatabase = WeatherEntity(
            0,
            model.current.dt,
            model.current.temp,
            model.current.pressure,
            model.current.humidity,
            model.current.clouds,
            model.current.wind_speed,
            model.current.weather[0].icon,
            model.current.weather[0].description,
            model.timezone,
            hoursListDB,
            daysListDB
        )
        return weatherDatabase
    } fun readFromDatabase() {
        homeViewModel.getWeather(requireContext()).observe(viewLifecycleOwner, Observer {
            it?.let{
                var sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context)
                if(sharedPreferences.getString("UNIT_SYSTEM", "")=="K") {
                    binding.tempreture.text = it.tempture.toString()+"°K"
                }
                else if(sharedPreferences.getString("UNIT_SYSTEM", "")=="C")
                {
                    binding.tempreture.text = it.tempture.toString()+"°C"
                }
                else
                {
                    binding.tempreture.text = it.tempture.toString()+"°F"
                }
                binding.pressure.text = it.pressure.toString()
                binding.dateHome.text = "${RetrofitInstance.dateNow}"
                binding.tempreture.text = it.tempture.toString()
                binding.humidity.text = it.humidity.toString() + "%"
                binding.cloud.text = it.clouds.toString()
                var city= it.city.split("/").toTypedArray()
                binding.cityName.text =city[1]
                binding.wind.text = it.wind_speed.toString()
                binding.discription.text = it.descrption
                binding.maxTep.text=it.dail_Weather[0].maxTemp.toString()
                binding.minTep.text=it.dail_Weather[0].minTemp.toString()
                var list: List<HoursEntity> = it.hour_Weather
                var listDaily: List<DaysEntity> = it.dail_Weather
                icon = it.icon
                context?.let {
                    Glide.with(it).load(getImage(icon)).into(binding.iconToday)
                    hoursAdapter.setData(list, it)
                    binding.hoursRecyclerview.adapter = hoursAdapter
                    dayAdapter.fetchData(listDaily, it)
                    binding.daysRecyclerview.adapter = dayAdapter
                }
            }
        })
    }

    fun viewWeather(latitude: String, longitude: String) {
        homeViewModel.fetchweather(latitude, longitude).observe(viewLifecycleOwner, Observer {
            var weatherDatabase = writeIntoDatabase(it)
            GlobalScope.launch {
                Dispatchers.IO
                homeViewModel.weatherDatabase(weatherDatabase, requireContext())
                withContext(Dispatchers.Main) {
                    readFromDatabase()
                }
            }
        })
    }

    private fun settings() {
        /*  val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
           //  val sw = findPreference("USE_DEVICE_LOCATION") as SwitchPreferenceCompat
             val sLocation:Boolean = sp.getBoolean("USE_DEVICE_LOCATION",true)
             if (sLocation){

             }*/
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val unitSystem = sharedPreferences.getString("UNIT_SYSTEM", "")
        val languageSystem = sharedPreferences.getString("LANGUAGE_SYSTEM", "")
        val location1 = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)
        val locations = sharedPreferences.getString("CUSTOM_LOCATION", "")
        if (unitSystem != null) {
            Setting.unitSystem = unitSystem
        }
        if (languageSystem != null) {
            Setting.languageSystem = languageSystem
        }
        if (location1 != null) {
            Setting.deviceLocation = location1
        }
        if (locations != null) {
            Setting.customLocations = locations
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
                .apply {
                    arguments = Bundle().apply {

                    }
                }


    }

}