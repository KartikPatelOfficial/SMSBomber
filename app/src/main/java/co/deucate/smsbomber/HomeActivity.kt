package co.deucate.smsbomber

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog
import com.google.android.gms.ads.*
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {

    private lateinit var mPhoneNumber: String
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPhoneEt: EditText
    private lateinit var mPhoneLayout: LinearLayout
    private lateinit var adRequest: AdRequest

    private lateinit var mStatus: TextView

    private var interstitialAd: InterstitialAd? = null
    private lateinit var logStrings: ArrayList<String>
    private lateinit var tempStrings:ArrayList<String>

    internal var currentTime: Date? = null
    internal var a: Int = 0
    private var current: Int = 0
    private var latest: Int = 0

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logStrings = ArrayList()
        mRecyclerView = findViewById(R.id.mainRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
        mRecyclerView.adapter = LogAdapter(logStrings)
        mPhoneEt = findViewById(R.id.mainPhoneEt)
        mPhoneLayout = findViewById(R.id.linearLayout)
        mStatus = findViewById(R.id.mainStatus)

        dataChange("> Hack Started:")

        if (!isNetworkAvailable) {
            dataChange("Err: Please connect to network")
            return
        }

        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            current = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            dataChange("Err: Data not found!!!")
            e.printStackTrace()
        }

        getLatestVersion()

        adRequest = AdRequest.Builder().build()

        Handler().postDelayed({
           showAd()
        }, 60000)

        AwesomeNoticeDialog(this)
                .setTitle("Warning")
                .setMessage("I(Developer of this app) is not responsible for any thing you did with this app. This app is only for prank. If you are not agree with my term and condition please don't use this app. In case you report my app or me i can take action to you.")
                .setColoredCircle(R.color.dialogWarningBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(true)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setButtonBackgroundColor(R.color.dialogWarningBackgroundColor)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setNoticeButtonClick { }
                .show()

        val adView = findViewById<AdView>(R.id.mainBottomBannerAd)
        val adRequest1 = AdRequest.Builder().build()
        adView.loadAd(adRequest1)

        MobileAds.initialize(this, "ca-app-pub-8086732239748075~8890173650")

        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adUnitId = "ca-app-pub-8086732239748075/9598708915"
        interstitialAd!!.loadAd(AdRequest.Builder().build())

        findViewById<View>(R.id.mainOkBtn).setOnClickListener(View.OnClickListener {
            mPhoneNumber = mPhoneEt.text.toString()

            if (TextUtils.isEmpty(mPhoneNumber)) {
                dataChange("??? Please enter mobile number")
                Toast.makeText(this@HomeActivity,"isEmpty",Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (!isNetworkAvailable) {
                dataChange("Err: Please connect to network")
                Toast.makeText(this@HomeActivity,"Network",Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            showAd()

            if (isDeveloperNumber(mPhoneNumber)) {
                dataChange("Err: Bombing on creator of this app does not make sense.")
                Toast.makeText(this@HomeActivity,"Developer",Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            getCurrentTime()
            isProtectedNumber()
        })

        findViewById<View>(R.id.contactBtn).setOnClickListener(View.OnClickListener {
            if (!isNetworkAvailable) {
                dataChange("Err: Please connect to network")
                return@OnClickListener
            }

            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, REQUEST_CONTACT_NUMBER)
        })

        interstitialAd!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                dataChange("Server up")
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                dataChange("Err: Errorcode : $errorCode")
                interstitialAd!!.loadAd(adRequest)
            }

            override fun onAdOpened() {}
            override fun onAdLeftApplication() {}

            override fun onAdClosed() {
                interstitialAd!!.loadAd(adRequest)
            }
        }


    }

    @SuppressLint("SimpleDateFormat")
    private fun isProtectedNumber() {

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Protected").document(mPhoneNumber).get().addOnCompleteListener { task ->
            val snapshot = task.result
            if (snapshot.exists()) {

                var timeString = snapshot.getString("Time")
                timeString = timeString!!.replace("T", " ")
                timeString = timeString.replace("Z", " ")
                val dateFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:SS.SSS")

                try {
                    val temp = dateFormat2.parse(timeString)

                    val difference = temp.time - currentTime!!.time
                    val days = (difference / (1000 * 60 * 60 * 24)).toInt().toLong()
                    var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt().toLong()
                    hours = if (hours < 0) -hours else hours

                    if (hours >= 3) {
                        DataHalper(mPhoneNumber).flipkart()
                    } else {
                        dataChange("??? This number is protected please tray again after some while.")
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }


            } else {
                DataHalper(mPhoneNumber).flipkart()
            }
        }
    }

    private fun getCurrentTime() {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://us-central1-smsbomber-e784b.cloudfunctions.net/Time").build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @SuppressLint("SimpleDateFormat")
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {


                val jsonData = response.body()!!.string()

                try {
                    val root = JSONObject(jsonData)
                    var timeString = root.getString("Time")

                    timeString = timeString.replace("T", " ")
                    timeString = timeString.replace("Z", " ")

                    val dateFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:SS.SSS")
                    val temp = dateFormat2.parse(timeString)
                    currentTime = temp

                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun getLatestVersion() {

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Current").document("version").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                latest = Integer.parseInt(snapshot.getString("v"))

                if (current != latest) {
                    showErrorDialog()
                }

            }
        }

    }

    private fun showErrorDialog() {

        AwesomeErrorDialog(this)
                .setTitle(R.string.app_name)
                .setMessage("Your app is not up to date please update you app to get latest feature.")
                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setErrorButtonClick {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://smsbomber.deucate.com/others/latest.apk")
                }
                .show()
    }

    private fun isDeveloperNumber(phoneNumber: String): Boolean {
        var mainPhoneNumber = phoneNumber

        mainPhoneNumber = mainPhoneNumber.replace(" ", "")
        val number = mainPhoneNumber.toCharArray()
        val myNumber = "9664769226".toCharArray()


        for (i in 0..9) {
            if (number[i] != myNumber[i]) {
                return false
            }
        }

        return true
    }

    fun updateStatus(s: String) {
        runOnUiThread({
            mStatus.text = s
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.overflow, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuAbout -> {
                dataChange("Created by\n" + getString(R.string.credit))
                return true
            }

            R.id.menuWeb -> {
                dataChange("Thank you for open my website. God bless you with 100 child.")
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://deucate.com/")
                startActivity(intent)
                return true
            }

            R.id.menuProtect -> {
                startActivity(Intent(this@HomeActivity, ProtectedActivity::class.java))
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CONTACT_NUMBER) {

            showAd()

            val uri = data.data
            @SuppressLint("Recycle")
            val cursor = contentResolver.query(uri!!, null, null, null, null)!!
            cursor.moveToFirst()
            val colum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            var numberT = cursor.getString(colum)
            val number: String
            if (numberT.contains("+")) {
                numberT = numberT.substring(3)
                number = numberT.replace(" ".toRegex(), "")
                mPhoneNumber = number
                mPhoneEt.setText(number)
            } else {
                number = numberT.replace(" ".toRegex(), "")
                mPhoneNumber = number
                mPhoneEt.setText(mPhoneNumber)
            }

            if (isDeveloperNumber(mPhoneNumber)) {
                dataChange("Bombing on creator of this app dosen't make sence. :(")
                return
            }
            mPhoneNumber = numberT
            mPhoneEt.setText(numberT)
            DataHalper(mPhoneNumber).flipkart()
        }

    }

    private fun showAd(){
        if (interstitialAd!!.isLoaded) {
            interstitialAd!!.show()
            interstitialAd!!.loadAd(adRequest)

        } else {
            dataChange("??? Wait for 10-15 second.")
            interstitialAd!!.loadAd(adRequest)
            return
        }
    }

    fun dataChange(log: String) {
        tempStrings = logStrings
        tempStrings.add(log)
        logStrings = tempStrings
        mRecyclerView.adapter.notifyDataSetChanged()
    }

    companion object {
        private const val REQUEST_CONTACT_NUMBER = 32
    }

}
