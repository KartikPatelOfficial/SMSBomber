package co.deucate.smsbomber.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.deucate.smsbomber.R

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(intent.getBooleanExtra("NIGHT_MODE",false)){
            setTheme(R.style.DarkMode)
        }else{
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction().replace(R.id.container, SettingsFragment()).commit()
    }
}
