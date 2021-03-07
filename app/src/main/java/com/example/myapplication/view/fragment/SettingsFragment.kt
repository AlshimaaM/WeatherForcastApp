package com.example.myapplication.view.fragment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.preference.*
import com.example.myapplication.R
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.ContextUtils.Companion.setLocal
import com.example.myapplication.view.activity.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.perferences, rootKey)
        preferenceManager.findPreference<Preference>("LANGUAGE_SYSTEM")!!
                .setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener { preference, newValue ->

                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    return@OnPreferenceChangeListener true
                })
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
         //   editSettings()
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
        fun editSettings(){
          //  val sp : SharedPrefrence = SharedPrefrence(requireActivity())
            val LP = findPreference("LANGUAGE_SYSTEM") as ListPreference?
            val lan = Setting.getLocalLanguage(requireContext())
            if ("ar".equals(lan)) {
                setLocal(requireActivity(), "ar")
                LP?.setSummary(LP?.getEntry())

            }else {
                setLocal(requireActivity(), "en")
               LP?.setSummary(LP?.getEntry())
            }

    LP!!.setOnPreferenceChangeListener(androidx.preference.Preference.OnPreferenceChangeListener { prefs, obj ->
        val items = obj as String
            if (prefs.key == "LANGUAGE_SYSTEM") {
                when (items) {
                    "ar" ->
                    { Setting.setLocalLanguage("ar", requireContext())
                       activity?.let { it1 -> setLocal(it1, "ar") }
                      startActivity(Intent(requireContext(), MainActivity::class.java))}
                    "en" -> {
                        Setting.setLocalLanguage("en", requireContext())
                        activity?.let { it1 -> setLocal(it1, "en") }
                        startActivity(Intent(requireContext(), MainActivity::class.java))}
                }
                     val UU = prefs as ListPreference
                     UU.summary = UU.entries[UU.findIndexOfValue(items)]
             }
                    true
                    })
        }

}


