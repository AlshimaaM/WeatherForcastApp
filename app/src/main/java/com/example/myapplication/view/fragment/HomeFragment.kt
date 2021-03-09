package com.example.myapplication.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapter.DayAdapter
import com.example.myapplication.adapter.HoursAdapter
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.remote.RetrofitInstance.getImage
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.provider.Setting
import com.example.myapplication.provider.AlertWork
import com.example.myapplication.receiver.AlertReceiver
import com.example.myapplication.util.ContextUtils.Companion.setLocale
import com.example.myapplication.util.ContextUtils.Companion.settings
import com.example.myapplication.util.Dialogs
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
    private lateinit var mProgress: Dialog
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        init()
       settings(requireContext())
        if (sharedPreferences.getBoolean("USE_DEVICE_LOCATION", true)) {
            getLastLocation()
            setUpAlerts()
        } else {
            Setting.latitude =  prefs.getString("lat"," ")!!
            Setting.longitude = prefs.getString("lon"," ")!!
            setUpAlerts()
        }
        if (homeViewModel.internetAvailable(requireContext())) {
            viewWeather(Setting.latitude!!, Setting.longitude!!)
        } else {
            readFromDatabase()
        }
        setUpAlerts()
        return binding.root
        }

   fun init(){
       mProgress = Dialogs.createProgressBarDialog(context, "")
       binding.hoursRecyclerview.layoutManager =
               LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
       binding.daysRecyclerview.layoutManager =
               LinearLayoutManager(context, RecyclerView.VERTICAL, false)
       hoursAdapter = HoursAdapter()
       dayAdapter = DayAdapter()
       sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
       prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
       editor = sharedPreferences.edit()
       homeViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
       mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
       workManager = WorkManager.getInstance(requireContext())
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                       requireContext())
       )
       {
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
    @SuppressLint("SetTextI18n")
    fun readFromDatabase() {
       // mProgress.show()
        homeViewModel.getWeather(requireContext()).observe(viewLifecycleOwner, Observer {
            it?.let {
                    if (sharedPreferences.getString("UNIT_SYSTEM","metric").equals("metric")) {
                    binding.tempreture.text = it.tempture.toString() + "°C"
                        binding.wind.text = it.wind_speed.toString()+ " " +"m/s"
                    } else if (sharedPreferences.getString("UNIT_SYSTEM","").equals("standard")) {
                    binding.tempreture.text = it.tempture.toString() + "°K"
                        binding.wind.text = it.wind_speed.toString()+ " " + "m/s"
                } else {
                    binding.tempreture.text = it.tempture.toString() + "°F"
                        binding.wind.text = it.wind_speed.toString()+ " " + "m/h"
                }
                binding.pressure.text = it.pressure.toString()+ " hPa"
                binding.dateHome.text = "${RetrofitInstance.dateNow}"
                binding.humidity.text = it.humidity.toString() + "%"
                binding.cloud.text = it.clouds.toString()
                var city = it.city.split("/").toTypedArray()
                binding.cityName.text = city[1]
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
           // mProgress.dismiss()
        })
    }

    fun viewWeather(latitude: String, longitude: String) {
     //   mProgress.show()
        setLocale(requireActivity(),Setting.languageSystem)
        homeViewModel.fetchweather(latitude, longitude).observe(viewLifecycleOwner, Observer {
            var weatherDatabase =homeViewModel.writeIntoDatabase(it)
            uiScope.launch {
                homeViewModel.weatherDatabase(weatherDatabase, requireContext())
                withContext(Dispatchers.Main) {
                    readFromDatabase()
                  //  mProgress.dismiss()
                }
            }
        })
    }
    private fun setUpAlerts() {
        if (sharedPreferences.getBoolean("ALERT", true) && prefs.getString("alerts", "yes")
                .equals("yes")
        ) {
            setUpFetchFromApiWorker()
            editor.putString("alerts", "no")
            editor.commit()
            editor.apply()
        } else if (!sharedPreferences.getBoolean("ALERT", true)) {
            val requestCodeListJson = prefs.getString("requestsOfAlerts", " ")
            val type: Type = object : TypeToken<List<Int>>() {}.type

            if (Gson().fromJson<List<Int>>(requestCodeListJson, type) != null) {
                var requestCodeList: List<Int> = Gson().fromJson(requestCodeListJson, type)
                for (requestCodeItem in requestCodeList) {
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
        val data: Data = Data.Builder().putString("lat", Setting.latitude).putString("lon", Setting.longitude
        ).putString("lang", Setting.languageSystem).putString("units", Setting.unitSystem)
            .build()
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
        workManager.getWorkInfoByIdLiveData(repeatingRequest.id).observe(viewLifecycleOwner,
            androidx.lifecycle.Observer {
            })
    }
    private fun locationNotEnable() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.loc_not_enable))
        alertDialogBuilder.setMessage(getString(R.string.location_dialog_message))
        alertDialogBuilder.setPositiveButton(getString(R.string.enable)) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            ActivityCompat.startActivityForResult(requireActivity(), intent, PERMISSION_ID, Bundle())
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
*/
    override fun onResume() {
        super.onResume()
        if (checkPermissions()){
            viewWeather(Setting.latitude,Setting.longitude)
        }
    }
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