package com.example.myapplication.view.fragment

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.os.Build
import android.os.Bundle
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
import com.example.myapplication.R
import com.example.myapplication.adapter.AlertAdapter
import com.example.myapplication.data.local.database.entity.AlertEntity
import com.example.myapplication.data.remote.RetrofitInstance.formateTime
import com.example.myapplication.databinding.FragmentAlertsBinding
import com.example.myapplication.model.AlertsItem
import com.example.myapplication.receiver.AlertReceiver
import com.example.myapplication.receiver.DialogReceiver
import com.example.myapplication.viewmodel.AlertsViewModel
import com.example.myapplication.viewmodel.WeatherViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AlertsFragment : Fragment() {

    private lateinit var binding: FragmentAlertsBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var viewModel: WeatherViewModel
    private lateinit var alertViewModel: AlertsViewModel
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertList: List<AlertsItem>
    private var notificationOrAlarm = "notification"
    private lateinit var prefs: SharedPreferences
    var myHour: Int? = null
    var myMin: Int? = null
    var myYear: Int? = null
    var myMon: Int? = null
    var myDay: Int? = null
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + job)

    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences(
            "prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alertList = ArrayList()
        binding.alertRV.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.alertRV.setHasFixedSize(true)
        alertAdapter = AlertAdapter(requireContext())
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView( binding.alertRV)
        alertViewModel = ViewModelProvider(this).get(AlertsViewModel::class.java)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alerts, container, false)
        binding.radioGroupNOrA.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.notification) {
                notificationOrAlarm = "notification"
            } else {
                notificationOrAlarm = "alarm"
            }
        }
        binding.alertDate.setOnClickListener {
            getDate()
        }
        binding.alertTime.setOnClickListener {
            getTime()
        }

        init()
        viewModel.getWeather(requireContext()).observe(viewLifecycleOwner,{
            it?.let {
                if (it.alerts_Weather.isNotEmpty()) {
                    alertList = it.alerts_Weather
                }
            }
        })
        getAlertFromDB()
        binding.btnAdd.setOnClickListener {
           /* binding.alertTime.text
            binding.alertDate.text*/
            if (myHour != null && myMin != null && myDay != null && myMon != null && myYear != null) {
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")

                val date: String = myDay.toString() + "-" + myMon + "-" + myYear + " " + myHour + ":" + myMin
                val dateLong = sdf.parse(date)!!.time
                if (alertList.isNotEmpty()) {
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
                        setNotification(myHour!!, myMin!!, myDay!!, myMon!!, myYear!!, getString(R.string.noThing), getString(R.string.niceDay))
                    }else {
                        setAlaram(getString(R.string.noThing), getString(R.string.niceDay), myHour!!, myMin!!, myDay!!, myMon!!, myYear!!)
                    }
                }
            }else {
                Toast.makeText(requireActivity(), getString(R.string.please), Toast.LENGTH_LONG).show()
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
        val date = "$day/$month/$year $hour:$min"

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
        val random = Random()
        val int1 = random.nextInt(99)
        val pendingIntentA = PendingIntent.getBroadcast(context, int1, intentA, 0)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar[Calendar.MONTH] = month - 1
        calendar[Calendar.DATE] = day
        calendar[Calendar.YEAR] = year
        calendar[Calendar.SECOND] = 0
        val alarmtime: Long = calendar.timeInMillis
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmtime, pendingIntentA)
        Toast.makeText(context,getString(R.string.added), Toast.LENGTH_LONG).show()
        requireActivity().registerReceiver(DialogReceiver(), IntentFilter())
        val date = "$day/$month/$year $hour:$min"

        addAlert(int1, event, date, desc, true)
    }
    @SuppressLint("SetTextI18n")
    private fun getDate() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            requireContext(), { _, year, monthOfYear, dayOfMonth ->
                binding.alertDate.text = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
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
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val datetime = Calendar.getInstance()

        val tpd = TimePickerDialog(
            requireContext(),
                { view, h, m ->
                    c[Calendar.HOUR_OF_DAY] = h
                    c[Calendar.MINUTE] = m
                    if (c.timeInMillis >= datetime.timeInMillis) {
                        binding.alertTime.setText("" + h + ":" + m)
                        binding.alertTime.visibility = View.VISIBLE
                        myHour = h
                        myMin = m
                    } else {
                        Toast.makeText(requireActivity(), getString(R.string.invalid), Toast.LENGTH_LONG).show()
                        binding.alertTime.text = " "
                        binding.alertTime.visibility = View.VISIBLE
                    }
                }, hour, minute, false
        )
        tpd.show()
    }

    var itemTouchHelper: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(activity).setMessage(getString(R.string.deletAlert))
                    .setPositiveButton(getString(R.string.yes)
                    ) { dialog, id ->
                        val alertItemDeleted = alertAdapter.getItemByVH(viewHolder)
                        cancelAlarm(alertItemDeleted.requestCode)
                        deleteAlertFromDB(alertItemDeleted)
                        alertAdapter.removeAlertItem(viewHolder)
                    }
                        .setNegativeButton(getString(R.string.no),
                            { dialog, id ->
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