package com.example.myapplication.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.myapplication.R
import com.example.myapplication.util.ContextUtils.Companion.setLocal
import com.example.myapplication.view.activity.MainActivity

class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.perferences, rootKey)
        preferenceManager.findPreference<Preference>("LANGUAGE_SYSTEM")!!

          editSettings()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
    fun editSettings(){

        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val LP = findPreference("LANGUAGE_SYSTEM") as ListPreference?
       // val sw = findPreference<ListPreference>("USE_DEVICE_LOCATION")
        //val sLocation = sp.getString("USE_DEVICE_LOCATION","true")
        val lan = sp.getString("LANGUAGE_SYSTEM", "En")
        if ("AR".equals(lan)) {
            setLocal(requireActivity(), "ar")
            LP?.setSummary(LP?.getEntry())

        }else if("EN".equals(lan)){
            setLocal(requireActivity(), "en")
           LP?.setSummary(LP?.getEntry())
        }

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
                val LPP = prefs as ListPreference
                LPP.summary = LPP.entries[LPP.findIndexOfValue(items)]
            }
            true
        })
    }


}


