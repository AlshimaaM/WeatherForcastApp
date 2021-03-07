package com.example.myapplication.provider

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.remote.RetrofitInstance.formateTime
import com.example.myapplication.model.AlertsItem
import com.example.myapplication.model.Model
import com.example.myapplication.receiver.AlertReceiver
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
    private val mcontext = context
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

    @SuppressLint("CommitPrefEdits")
    fun init() {
        alarmManager = mcontext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = mcontext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

    }

    @SuppressLint("SimpleDateFormat")
    fun setAlarm(alerts: List<AlertsItem>) {
         java.text.SimpleDateFormat("EEE, h:mm a")
        if (alerts.isNotEmpty()) {
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
        val intent = Intent(mcontext, AlertReceiver::class.java)
        intent.putExtra("event", event)
        intent.putExtra("desc", description)
        val random = Random()
        val int1 = random.nextInt(99)

        val pendingIntent = PendingIntent.getBroadcast(mcontext, int1, intent, 0)
        requestCodeList.add(int1)
        val alertTime: Long = startTime.toLong()

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent)
        mcontext.registerReceiver(AlertReceiver(), IntentFilter())
    }
}