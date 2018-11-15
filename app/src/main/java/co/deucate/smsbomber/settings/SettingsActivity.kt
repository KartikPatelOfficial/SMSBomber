package co.deucate.smsbomber.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.deucate.smsbomber.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction().replace(R.id.container, SettingsFragment()).commit()
    }
}
