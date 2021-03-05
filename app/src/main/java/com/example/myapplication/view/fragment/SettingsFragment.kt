package com.example.myapplication.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.preference.*
import com.example.myapplication.R
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.ContextUtils.Companion.setLocal
import com.example.myapplication.view.activity.MainActivity

class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.perferences, rootKey)
        preferenceManager.findPreference<Preference>("LANGUAGE_SYSTEM")!!
        val sp= PreferenceManager.getDefaultSharedPreferences(context)
        val mapFragment: SwitchPreference?=findPreference("MAP_LOCATION")
        mapFragment?.onPreferenceClickListener= Preference.OnPreferenceClickListener {

            if(sp.getBoolean("MAP_LOCATION", true))
            {
                view?.findNavController()
                        ?.navigate(R.id.action_settingsFragment_to_mapSettingsFragment)
            }
            true
        }

          editSettings()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
    fun editSettings(){

        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val LP = findPreference("LANGUAGE_SYSTEM") as ListPreference?
        val uni = findPreference<ListPreference>("UNIT_SYSTEM")
        val lan = sp.getString("LANGUAGE_SYSTEM", "En")
        if ("AR".equals(lan)) {
            setLocal(requireActivity(), "ar")
            LP?.setSummary(LP?.getEntry())

        }else if("EN".equals(lan)){
            setLocal(requireActivity(), "en")
           LP?.setSummary(LP?.getEntry())
        }
        val un = sp.getString("UNIT_SYSTEM", "K")
        if ("K".equals(un)) {
            Setting.unitSystem="standard"
            uni?.setSummary(uni?.getEntry())

        }else if("C".equals(uni)){
            Setting.unitSystem="imperial"
            uni?.setSummary(uni?.getEntry())
        }
        else {
            Setting.unitSystem="metric"
            uni?.setSummary(uni?.getEntry())
        }
        uni!!.setOnPreferenceChangeListener(androidx.preference.Preference.OnPreferenceChangeListener { prefs, obj ->
            val items = obj as String
            if (prefs.key == "UNIT_SYSTEM") {
                when (items) {
                    "K" ->
                    { Setting.unitSystem="standard"
                        //units="standard"
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)}
                    "C" -> {Setting.unitSystem="metric"
                       // units="metric"
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)}
                    "F" -> {
                        Setting.unitSystem="imperial"
                        //units="imperial"
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)}
                }
                val LPP = prefs as ListPreference
                LPP.summary = LPP.entries[LPP.findIndexOfValue(items)]
            }
            true
        })
        LP!!.setOnPreferenceChangeListener(androidx.preference.Preference.OnPreferenceChangeListener { prefs, obj ->
            val items = obj as String
            if (prefs.key == "LANGUAGE_SYSTEM") {
                when (items) {
                    "AR" ->
                    { setLocal(requireActivity(), "ar")
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)}
                    "EN" -> {
                        setLocal(requireActivity(), "en")
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)}
                }
                val UU = prefs as ListPreference
                UU.summary = UU.entries[UU.findIndexOfValue(items)]
            }
            true
        })
    }


}


