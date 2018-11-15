package co.deucate.smsbomber

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

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

    private lateinit var mEditText: EditText
    private lateinit var mButton: Button
    private lateinit var mNumber: String


    private var isError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protected)

        mButton = findViewById(R.id.protectNumberBtn)
        mButton.isEnabled = false

        mEditText = findViewById(R.id.protectNumberET)

        mButton.setOnClickListener {
            mNumber = mEditText.text.toString()

            if (isError) {
                try {
                    startAddNumber()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

    }


    @Throws(IOException::class)
    private fun startAddNumber() {

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
