package co.deucate.smsbomber.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import co.deucate.smsbomber.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import co.deucate.smsbomber.HomeActivity


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefrences, rootKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val darkModeSwitch = preferenceManager.findPreference("darkMode")

        if (darkModeSwitch.isEnabled) {
            activity!!.setTheme(R.style.DarkMode)
        } else {
            activity!!.setTheme(R.style.AppTheme)
        }

        darkModeSwitch.setOnPreferenceChangeListener { _, newValue ->

            if (newValue as Boolean) {
                setTheme(activity!!, true)
            } else {
                setTheme(activity!!, false)
            }
            true
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setTheme(context: Context, theme: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean("NIGHT_MODE", theme).apply()
        HomeActivity.isNightModeEnabled = theme
        HomeActivity.recreated = false
        if (theme) {
            activity!!.setTheme(R.style.DarkMode)
        } else {
            activity!!.setTheme(R.style.AppTheme)
        }
    }

}