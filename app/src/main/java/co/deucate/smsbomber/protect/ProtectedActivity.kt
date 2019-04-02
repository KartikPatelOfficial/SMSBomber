package co.deucate.smsbomber.protect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import co.deucate.smsbomber.R

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra("NIGHT_MODE", false)) {
            setTheme(R.style.DarkMode)
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_protected)

        findViewById<Button>(R.id.protectNumberBtn).setOnClickListener {

            val number = findViewById<EditText>(R.id.protectNumberET).text.toString()

            try {
                startAddNumber(number)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }


    @Throws(IOException::class)
    private fun startAddNumber(mNumber: String) {

        val client = OkHttpClient()
        val request = Request.Builder().url("https://us-central1-smsbomber-e784b.cloudfunctions.net/Time").build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body()!!.string()
                var data = HashMap<String, Any>()

                try {
                    val root = JSONObject(jsonData)
                    val time = root.get("Time")
                    val temp = HashMap<String, Any>()
                    temp["Time"] = time
                    data = temp
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("Protected").document(mNumber).set(data).addOnCompleteListener { }

            }
        })


    }
}
