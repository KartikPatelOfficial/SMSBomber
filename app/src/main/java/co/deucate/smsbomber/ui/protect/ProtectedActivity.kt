package co.deucate.smsbomber.ui.protect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import co.deucate.smsbomber.R
import co.deucate.smsbomber.service.ProtectedNumberService

import com.google.firebase.firestore.FirebaseFirestore

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.util.HashMap

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class ProtectedActivity : AppCompatActivity() {

    private val protectedNumberService = ProtectedNumberService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protected)

        findViewById<Button>(R.id.protectNumberBtn).setOnClickListener {
            val number = findViewById<EditText>(R.id.protectNumberET).text.toString().replace(" ", "").replace("+91", "")
            protectedNumberService.addProtectedNumber(number)
        }
    }

}
