package co.deucate.smsbomber

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
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

class ProtectedActivity : AppCompatActivity(), RewardedVideoAdListener {

    private lateinit var mEditText: EditText
    private lateinit var mButton: Button
    private lateinit var mNumber: String

    private var mRewardedVideoAd: RewardedVideoAd? = null

    private var isError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protected)

        val mAdView = findViewById<AdView>(R.id.protectedAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val mAdView1 = findViewById<AdView>(R.id.protectedAdViewLarge)
        val adRequest1 = AdRequest.Builder().build()
        mAdView1.loadAd(adRequest1)

        MobileAds.initialize(this, "ca-app-pub-8086732239748075~8890173650")

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd!!.rewardedVideoAdListener = this
        loadRewardedVideoAd()

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

            if (mRewardedVideoAd!!.isLoaded) {
                mRewardedVideoAd!!.show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.overflow, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menuAbout -> {
                Toast.makeText(this, "Created by spidy0471", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.menuWeb -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://deucate.com/")
                startActivity(intent)
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

    }

    private fun loadRewardedVideoAd() {
        mRewardedVideoAd!!.loadAd("ca-app-pub-8086732239748075/7406638658", AdRequest.Builder().build())
    }

    override fun onRewardedVideoAdLoaded() {
        mButton.isEnabled = true

    }

    override fun onRewardedVideoAdOpened() {

    }

    override fun onRewardedVideoStarted() {

    }

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideoAd()
    }

    override fun onRewarded(rewardItem: RewardItem) {
        try {
            startAddNumber()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdLeftApplication() {
        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdFailedToLoad(i: Int) {
        loadRewardedVideoAd()
        Toast.makeText(this, "Error: $i", Toast.LENGTH_SHORT).show()
        isError = true
    }

    override fun onRewardedVideoCompleted() {

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

    public override fun onResume() {
        mRewardedVideoAd!!.resume(this)
        super.onResume()
    }

    public override fun onPause() {
        mRewardedVideoAd!!.pause(this)
        super.onPause()
    }

    public override fun onDestroy() {
        mRewardedVideoAd!!.destroy(this)
        super.onDestroy()
    }

}
