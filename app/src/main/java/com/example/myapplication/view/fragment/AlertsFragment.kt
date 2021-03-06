package com.example.myapplication.view.fragment

import android.app.*
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.FinalProject2.data.receiver.AlertReceiver
import com.example.FinalProject2.data.receiver.DialogReceiver
import com.example.myapplication.R
import com.example.myapplication.adapter.AlertAdapter
import com.example.myapplication.data.local.database.entity.AlertEntity
import com.example.myapplication.data.remote.RetrofitInstance.formateTime
import com.example.myapplication.databinding.FragmentAlertsBinding
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.AlertsItem
import com.example.myapplication.model.Model
import com.example.myapplication.provider.Setting
import com.example.myapplication.viewmodel.AlertsViewModel
import com.example.myapplication.viewmodel.WeatherViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AlertsFragment : Fragment() {
    var myHour: Int? = null
    var myMin: Int? = null
    var myYear: Int? = null
    var myMon: Int? = null
    var myDay: Int? = null
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var alarmManager: AlarmManager
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private lateinit var viewModel: WeatherViewModel
    private lateinit var alertViewModel: AlertsViewModel
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertList: List<AlertsItem>
    private var notificationOrAlarm = "notification"
    lateinit var prefs: SharedPreferences
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + job)

    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences(
            "prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        alarmManager =
            requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alertList = ArrayList()
        binding.alertRV.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.alertRV.setHasFixedSize(true)
        alertAdapter = AlertAdapter(requireContext())
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView( binding.alertRV)
        alertViewModel = ViewModelProvider(this).get(AlertsViewModel::class.java)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alerts, container, false)
        binding.alertDate.setOnClickListener {
            getDate()
        }
        binding.alertTime.setOnClickListener {
            getTime()
        }
        binding.radioGroupNOrA.setOnCheckedChangeListener({ group, checkedId ->
                if (checkedId == R.id.notification) {
                    notificationOrAlarm = "notification"
                } else {
                    notificationOrAlarm = "alarm"
                }
            })
        init()
        viewModel.getWeather(requireContext()).observe(viewLifecycleOwner,{
            it?.let {
                if (it.alerts_Weather.size > 0) {
                    alertList = it.alerts_Weather
                }
            }
        })
        getAlertFromDB()
        binding.btnAdd.setOnClickListener {
            binding.alertTime.text
            binding.alertDate.text
            if (myHour != null && myMin != null && myDay != null && myMon != null && myYear != null) {
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")

                val date: String =
                    myDay.toString() + "-" + myMon + "-" + myYear + " " + myHour + ":" + myMin
                val dateLong = sdf.parse(date)!!.time
                if (alertList.size > 0) {
                    for (alertItem in alertList) {
                        if (dateLong / 1000 > alertItem.start && dateLong / 1000 < alertItem.end) {
                            if (notificationOrAlarm.equals("notification")) {
                                setNotification(myHour!!, myMin!!, myDay!!, myMon!!, myYear!!, alertItem.event,
                                    "From ${formateTime(alertItem.start)} to ${formateTime(alertItem.end)}"
                                )
                            } else {
                                setAlaram(alertItem.event, alertItem.description, myHour!!, myMin!!, myDay!!, myMon!!, myYear!!)
                            }
                            break
                        }
                    }
                }else{
                    if (notificationOrAlarm.equals("notification")) {
                        setNotification(myHour!!, myMin!!, myDay!!, myMon!!, myYear!!, "NO Thing", "Nice Day")
                    }else {
                        setAlaram("NO Thing", "Nice Day", myHour!!, myMin!!, myDay!!, myMon!!, myYear!!)
                    }
                }
            }else {
                Toast.makeText(requireActivity(), "Please Enter Valid Data", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setNotification(hour: Int, min: Int, day: Int, month: Int, year: Int, event: String, description: String) {
        val intentA = Intent(context, AlertReceiver::class.java)
        intentA.putExtra("event", event)
        intentA.putExtra("desc", description)
        val r = Random()
        val i1 = r.nextInt(99)
        val pendingIntentA = PendingIntent.getBroadcast(context, i1, intentA, 0)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar[Calendar.MONTH] = month - 1
        calendar[Calendar.DATE] = day
        calendar[Calendar.YEAR] = year
        calendar[Calendar.SECOND] = 0
        val alarmtime: Long = calendar.timeInMillis
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmtime, pendingIntentA)
        Toast.makeText(context,"Done!", Toast.LENGTH_LONG).show()
        requireActivity().registerReceiver(AlertReceiver(), IntentFilter())
        var date = day.toString() + "/" + month + "/" + year + " " + hour + ":" + min

        addAlert(i1, event, date, description, true)
    }
    private fun getAlertFromDB() {
        alertViewModel.getAlert(requireContext()).observe(viewLifecycleOwner,  {
            it?.let {
                alertAdapter.fetchData(it, requireContext())
                binding.alertRV.adapter = alertAdapter
            }
        })
    }

    private fun addAlert(requestCode: Int, event: String, start: String, description: String, status: Boolean) {
        val alert = AlertEntity(requestCode, event, start, description, status)
        uiScope.launch {
            alertViewModel.addAlert(alert,requireContext())
        }


    }
    private fun setAlaram(
        event: String, desc: String, hour: Int,
        min: Int, day: Int, month: Int, year: Int) {
        val intentA = Intent(context, DialogReceiver::class.java)
        intentA.putExtra("event", event)
        intentA.putExtra("desc", desc)
        val r = Random()
        val i1 = r.nextInt(99)
        val pendingIntentA = PendingIntent.getBroadcast(context, i1, intentA, 0)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar[Calendar.MONTH] = month - 1
        calendar[Calendar.DATE] = day
        calendar[Calendar.YEAR] = year
        calendar[Calendar.SECOND] = 0
        val alarmtime: Long = calendar.timeInMillis
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmtime, pendingIntentA)
        Toast.makeText(context,"Done!", Toast.LENGTH_LONG).show()
        requireActivity().registerReceiver(DialogReceiver(), IntentFilter())
        var date = day.toString() + "/" + month + "/" + year + " " + hour + ":" + min

        addAlert(i1, event, date, desc, true)
    }
    private fun getDate() {
        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            requireContext(), { view, year, monthOfYear, dayOfMonth ->
                binding.alertDate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                binding.alertDate.visibility = View.VISIBLE

                myMon = monthOfYear + 1
                myYear = year
                myDay = dayOfMonth
            }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun getTime() {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val datetime = Calendar.getInstance()

        val tpd = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
                c[Calendar.HOUR_OF_DAY] = h
                c[Calendar.MINUTE] = m
                if (c.timeInMillis >= datetime.timeInMillis) {
                    binding.alertTime.setText("" + h + ":" + m)
                    binding.alertTime.visibility = View.VISIBLE
                    myHour = h
                    myMin = m
                } else {
                    Toast.makeText(requireActivity(), "Invalide Data", Toast.LENGTH_LONG).show()
                    binding.alertTime.setText(" ")
                    binding.alertTime.visibility = View.VISIBLE
                }
            }), hour, minute, false
        )
        tpd.show()
    }

    var itemTouchHelper: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(activity).setMessage("Do You Want to Delete this Alert ?!")
                    .setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialog, id ->
                            val alertItemDeleted = alertAdapter.getItemByVH(viewHolder)
                            cancelAlarm(alertItemDeleted.requestCode)
                            deleteAlertFromDB(alertItemDeleted)
                            alertAdapter.removeAlertItem(viewHolder)
                        })
                    .setNegativeButton("No",
                        DialogInterface.OnClickListener { dialog, id ->
                            getAlertFromDB()
                        }).show()

            }
        }

    fun deleteAlertFromDB(alertDB: AlertEntity) {
        alertViewModel.deleteAlert(alertDB,requireContext())
    }

    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(requireContext(), AlertReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}