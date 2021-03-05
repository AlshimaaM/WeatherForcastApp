package com.example.myapplication.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.drm.ProcessedData
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.FinalProject2.data.receiver.AlertReceiver
import com.example.myapplication.HandlingLocation
import com.example.myapplication.R
import com.example.myapplication.adapter.DayAdapter
import com.example.myapplication.adapter.HoursAdapter
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.local.database.entity.WeatherEntity
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.remote.RetrofitInstance.getImage
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.AlertsItem
import com.example.myapplication.model.Model
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.AlertWork
import com.example.myapplication.viewmodel.WeatherViewModel
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class HomeFragment :  Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: WeatherViewModel
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var dayAdapter: DayAdapter
    val PERMISSION_ID = 42
    private lateinit var icon: String
    private lateinit var workManager: WorkManager
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        init()
        settings()
        if (sharedPreferences.getBoolean("USE_DEVICE_LOCATION", true)) {
            getLastLocation()
            setUpAlerts()
        } else {
            Setting.latitude =  prefs.getString("lat"," ")!!
            Setting.longitude = prefs.getString("lon"," ")!!
            setUpAlerts()
        }
           viewWeather(Setting.latitude, Setting.longitude)

        setUpAlerts()
        return binding.root
        }

   fun init(){
       binding.hoursRecyclerview.layoutManager =
               LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
       binding.daysRecyclerview.layoutManager =
               LinearLayoutManager(context, RecyclerView.VERTICAL, false)
       hoursAdapter = HoursAdapter()
       dayAdapter = DayAdapter()

       mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
       homeViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
       workManager = WorkManager.getInstance(requireContext())
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                       requireContext()
               )
       ) {
           val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                   Uri.parse("package:" + requireActivity().getPackageName())
           )
           startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
       }

   }

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
                        viewWeather(Setting.latitude, Setting.longitude)
                    }
                }
            } else {
                locationNotEnable()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
    fun readFromDatabase() {
        homeViewModel.getWeather(requireContext()).observe(viewLifecycleOwner, Observer {
           /*   if (it==null){
                firstTime()
            }else {*/
            it?.let {
                var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                if (sharedPreferences.getString("UNIT_SYSTEM", "") == "K") {
                    binding.tempreture.text = it.tempture.toString() + "°K"
                } else if (sharedPreferences.getString("UNIT_SYSTEM", "") == "C") {
                    binding.tempreture.text = it.tempture.toString() + "°C"
                } else {
                    binding.tempreture.text = it.tempture.toString() + "°F"
                }
                binding.pressure.text = it.pressure.toString()
                binding.dateHome.text = "${RetrofitInstance.dateNow}"
                binding.tempreture.text = it.tempture.toString()
                binding.humidity.text = it.humidity.toString() + "%"
                binding.cloud.text = it.clouds.toString()
                var city = it.city.split("/").toTypedArray()
                binding.cityName.text = city[1]
                binding.wind.text = it.wind_speed.toString()
                binding.discription.text = it.descrption
                binding.maxTep.text = it.dail_Weather[0].maxTemp.toString()
                binding.minTep.text = it.dail_Weather[0].minTemp.toString()
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
     //   }
        })
    }

    fun viewWeather(latitude: String, longitude: String) {
        homeViewModel.fetchweather(latitude, longitude).observe(viewLifecycleOwner, Observer {
            var weatherDatabase =homeViewModel.writeIntoDatabase(it)
            uiScope.launch {
                homeViewModel.weatherDatabase(weatherDatabase, requireContext())
                withContext(Dispatchers.Main) {
                    readFromDatabase()
                }
            }
        })
    }
    private fun setUpAlerts() {
        Log.v("homeTest", "here")
        if (sharedPreferences.getBoolean("ALERT", true) && prefs.getString("alerts", "yes")
                .equals("yes")
        ) {
            setUpFetchFromApiWorker()
            Log.v("homeTest", "here")
            editor.putString("alerts", "no")
            editor.commit()
            editor.apply()
        } else if (!sharedPreferences.getBoolean("ALERT", true)) {
            val requestCodeListJson = prefs.getString("requestsOfAlerts", " ")
            val type: Type = object : TypeToken<List<Int>>() {}.type

            if (Gson().fromJson<List<Int>>(requestCodeListJson, type) != null) {
                var requestCodeList: List<Int> = Gson().fromJson(requestCodeListJson, type)
                Log.v("cancel", requestCodeListJson.toString())
                for (requestCodeItem in requestCodeList) {
                    Log.v("cancel", "cancel")
                    cancelAlarm(requestCodeItem)

                }
                workManager.cancelAllWorkByTag("PeriodicWork")
                editor.putString("alerts", "yes")
                editor.commit()
                editor.apply()
            }

        }
    }

    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    private fun setUpFetchFromApiWorker() {
        Log.v("homeTest", "here")
        val data: Data = Data.Builder().putString("lat", Setting.latitude).putString(
            "lon",
            Setting.longitude
        ).putString("lang", Setting.languageSystem).putString("units", Setting.unitSystem)
            .build()
        Log.v("homeTest", Setting.latitude)
        val constrains = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val repeatingRequest = PeriodicWorkRequest.Builder(
            AlertWork::class.java, 1,
            TimeUnit.HOURS
        )
            .addTag("PeriodicWork")
            .setConstraints(constrains)
            .setInputData(data)
            .build()
        workManager.enqueue(repeatingRequest)
        workManager.getWorkInfoByIdLiveData(repeatingRequest.id).observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                Log.v("state", it.state.name)
            })
    }
    private fun settings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val unitSystem = sharedPreferences.getString("UNIT_SYSTEM", "")
        val languageSystem = sharedPreferences.getString("LANGUAGE_SYSTEM", "")
        val location1 = sharedPreferences.getBoolean("USE_DEVICE_LOCATION", false)
        //val notifications = sharedPreferences.getBoolean("USE_NOTIFICATIONS_ALERT", false)
        val locations = sharedPreferences.getString("CUSTOM_LOCATION", "")
        val mapLocation = sharedPreferences.getBoolean("MAP_LOCATION", false)
        prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        if (unitSystem != null) {
            Setting.unitSystem = unitSystem
        }
        if (languageSystem != null) {
            Setting.languageSystem = languageSystem
        }
        if (location1 != null) {
            Setting.deviceLocation = location1
        }
        /* if (notifications != null) {
             Settings.notifications = notifications
         }*/
        if (locations != null) {
            Setting.customLocations = locations
        }
        Setting.mapLocation = mapLocation!!
    }
    private fun locationNotEnable() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Location Not enable")
        alertDialogBuilder.setMessage("To load the current accurate temperature you have to enable location")
        alertDialogBuilder.setPositiveButton("Enable") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            ActivityCompat.startActivityForResult(requireActivity(), intent, PERMISSION_ID, Bundle())
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Load From Last Location Known") {dialog, which ->
                readFromDatabase()
            dialog.dismiss()
        }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }
    private fun firstTime() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("There is No data")
        alertDialogBuilder.setMessage("Because This is first time you should enable location to show data")
        alertDialogBuilder.setPositiveButton("Enable") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            ActivityCompat.startActivityForResult(context as Activity, intent, PERMISSION_ID, Bundle())
            dialog.dismiss()
        }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }
 /*   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PERMISSION_ID){
            getLastLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()){
          //  viewWeather(Setting.latitude,Setting.longitude)
            getLastLocation()
        }
    }*/
 override fun onDestroy() {
     super.onDestroy()
     job.cancel()
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