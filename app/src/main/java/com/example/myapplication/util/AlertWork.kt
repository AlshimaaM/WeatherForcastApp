package com.example.myapplication.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.FinalProject2.data.receiver.AlertReceiver
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.remote.RetrofitInstance.formateTime
import com.example.myapplication.model.AlertsItem
import com.example.myapplication.model.Model
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class AlertWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private var weatherMutableLiveDataApi: MutableLiveData<Model> = MutableLiveData()
    private var lat: String? = null
    private var lon: String? = null
    private var lang: String? = null
    private var units: String? = null
    private var windSpeed: String? = null
    private val mCtx = context
    private var requestCodeList = ArrayList<Int>()
    private val gson = Gson()
    private lateinit var alarmManager: AlarmManager
    private lateinit var alerts: List<AlertsItem>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun doWork(): Result {
        lat = inputData.getString("lat")
        lon = inputData.getString("lon")
        lang = inputData.getString("lang")
        units = inputData.getString("units")
        alerts = ArrayList()
        init()
        fetchWeather()
        return Result.success()
    }

    fun init() {
        alarmManager = mCtx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = mCtx.getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()

    }

    @SuppressLint("SimpleDateFormat")
    fun setAlarm(alerts: List<AlertsItem>) {
        val sdf = java.text.SimpleDateFormat("EEE, h:mm a")
        if (alerts.size > 0) {
            for (alertItem in alerts) {
                val now = System.currentTimeMillis()
                if (alertItem.start > now / 1000) {
                    setNotification(
                        alertItem.start,
                        alertItem.event,
                        "From ${formateTime(alertItem.start)} " +
                                "to ${formateTime(alertItem.end)}"
                    )
                } else if (alertItem.end > now / 1000) {
                    setNotification(
                        alertItem.end,
                        alertItem.event,
                        "From ${formateTime(alertItem.start)} " +
                                "to ${formateTime(alertItem.end)}"
                    )

                }

            }
            val requestCodeJson = gson.toJson(requestCodeList)
            editor.putString("requestsOfAlerts", requestCodeJson)
            editor.commit()
            editor.apply()
        }
    }
    fun fetchWeather(): MutableLiveData<Model> {
       CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance
                    .getCurrentLocationweather(
                        lat!!, lon!!, units!!, lang!!).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        weatherMutableLiveDataApi.value = response.body()
                        response.body()!!.alerts?.let {
                            setAlarm(it)
                        }
                        setAlarm(ArrayList<AlertsItem>())
                    }
                }
            } catch (e: Exception) {
            }
        }
        return weatherMutableLiveDataApi

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setNotification(startTime: Int, event: String, description: String) {
        val intent = Intent(mCtx, AlertReceiver::class.java)
        intent.putExtra("event", event)
        intent.putExtra("desc", description)
        val random = Random()
        val i1 = random.nextInt(99)

        val pendingIntent = PendingIntent.getBroadcast(mCtx, i1, intent, 0)
        requestCodeList.add(i1)
        val alertTime: Long = startTime.toLong()

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent)
        mCtx.registerReceiver(AlertReceiver(), IntentFilter())
    }
}