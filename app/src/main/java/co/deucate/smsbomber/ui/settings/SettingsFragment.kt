package co.deucate.smsbomber.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import co.deucate.smsbomber.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefrences, rootKey)
    }
}