package co.deucate.smsbomber

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import co.deucate.smsbomber.core.Bombs
import co.deucate.smsbomber.model.History
import co.deucate.smsbomber.ui.protect.ProtectedActivity
import co.deucate.smsbomber.ui.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HomeActivity : AppCompatActivity() {

    internal var currentTime: Date? = null

    private lateinit var databaseService: DatabaseService
    private val histories = ArrayList<History>()

    val adapter = Adapter(histories)

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.bottomBar))

        databaseService = DatabaseService(this)

        databaseService.getHistories {
            histories.clear()
            histories.addAll(it)
            adapter.notifyDataSetChanged()
        }

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            var background: Drawable? = null
            var xMark: Drawable? = null
            var xMarkMargin: Int = 1
            var initiated: Boolean = false

            private fun init() {
                background = ColorDrawable(Color.parseColor("#D32F2F"))
                xMark = ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_delete_black_24dp)
                xMark!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                initiated = true
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Toast.makeText(this@HomeActivity, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removedItem = histories[position]

                databaseService.deleteFromHistory(histories[position].index)
                histories.removeAt(position)
                adapter.notifyDataSetChanged()

                val sandbar = Snackbar.make(findViewById(R.id.coordinator), "Deleted Successfully", Snackbar.LENGTH_LONG).setAction("UNDO") {
                    addDataToDB(removedItem.number, removedItem.name)
                    adapter.notifyDataSetChanged()
                    val sandbar1 = Snackbar.make(findViewById(R.id.coordinator), "History is restored!", Snackbar.LENGTH_SHORT)
                    sandbar1.show()
                }

                sandbar.show()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView
                if (viewHolder.adapterPosition == -1) {
                    return
                }
                if (!initiated) {
                    init()
                }

                background!!.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background!!.draw(c)

                val xMarkLeft: Int
                val xMarkRight: Int
                val xMarkTop = itemView.top
                val xMarkBottom = xMarkTop + itemView.height
                if (dX < 0) {
                    xMarkLeft = itemView.right - xMarkMargin - 100
                    xMarkRight = itemView.right - xMarkMargin
                } else {
                    xMarkLeft = itemView.left + xMarkMargin
                    xMarkRight = itemView.left + xMarkMargin + 100
                }
                xMark!!.setBounds(xMarkLeft - (itemView.height / 2.5).roundToInt(), xMarkTop + 50, xMarkRight - 50, xMarkBottom - 50)
                xMark!!.draw(c)
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        adapter.listener = object : Adapter.OnClickCallback {

            override fun onClickCard(history: History) {
                AlertDialog.Builder(this@HomeActivity).setTitle("Warning!!!").setMessage("Are ou sure ou want to start bombing on ${history.number}?")
                        .setPositiveButton("YES") { _, _ ->
                            addDataToDB(history.number, history.name)
                            this@HomeActivity.histories.add(history)

                            this@HomeActivity.mainPhoneEt.text = SpannableStringBuilder.valueOf(history.number)

                            val helper = Bombs(history.number)
                            helper.listener = object : Bombs.OnCallBack {
                                override fun onFailListener(err: String) {
                                }

                                override fun onSuccessListener(res: String) {
                                    updateStatus(res)
                                }

                            }
                            helper.flipkart()
                            adapter.notifyDataSetChanged()

                        }.setNegativeButton("NO") { _, _ -> }.show()
            }
        }

        mainRecyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
        mainRecyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(mainRecyclerView)

        if (!isNetworkAvailable) {
            AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please connect to network")
                    .setPositiveButton("Ok") { _, _ -> }
                    .show()
            return
        }

        AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage(getString(R.string.warning))
                .setPositiveButton("Ok") { _, _ ->
                }
                .show()


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            startActivity(Intent(this, ProtectedActivity::class.java))
        }


        findViewById<View>(R.id.mainOkBtn).setOnClickListener(View.OnClickListener {
            val mPhoneNumber = mainPhoneEt.text.toString()

            if (TextUtils.isEmpty(mPhoneNumber)) {
                mainPhoneEt.error = "Please enter mobile number"
                return@OnClickListener
            }

            if (!isNetworkAvailable) {
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please connect to network")
                        .setPositiveButton("Ok") { _, _ -> }
                        .show()
                return@OnClickListener
            }

            if (isDeveloperNumber(mPhoneNumber)) {
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Bombing on developer of this app does not make sense... Logic Level 999999")
                        .setPositiveButton("Ok") { _, _ -> }
                        .show()
                return@OnClickListener
            }
            getCurrentTime()
            isProtectedNumber(mPhoneNumber)
        })

        findViewById<View>(R.id.contactBtn).setOnClickListener(View.OnClickListener {
            if (!isNetworkAvailable) {
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please connect to network")
                        .setPositiveButton("Ok") { _, _ -> }
                        .show()
                return@OnClickListener
            }

            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, REQUEST_CONTACT_NUMBER)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menuSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }

    @SuppressLint("SimpleDateFormat")
    private fun isProtectedNumber(mPhoneNumber: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Protected").document(mPhoneNumber).get().addOnCompleteListener { task ->
            val snapshot = task.result
            if (snapshot!!.exists()) {

                var timeString = snapshot.getString("Time")
                timeString = timeString!!.replace("T", " ").replace("Z", " ")
                val dateFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:SS.SSS")

                try {
                    val temp = dateFormat2.parse(timeString)

                    val difference = temp.time - currentTime!!.time
                    val days = (difference / (1000 * 60 * 60 * 24)).toInt().toLong()
                    var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt().toLong()
                    hours = if (hours < 0) -hours else hours

                    if (hours >= 3) {
                        val helper = Bombs(mPhoneNumber)
                        helper.listener = object : Bombs.OnCallBack {
                            override fun onFailListener(err: String) {
                            }

                            override fun onSuccessListener(res: String) {
                                updateStatus(res)
                            }

                        }
                        addDataToDB(mPhoneNumber, null)
                        helper.flipkart()
                    } else {
                        AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage("This number is protected please tray again after some while.")
                                .setPositiveButton("Ok") { _, _ -> }
                                .show()
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }


            } else {
                val helper = Bombs(mPhoneNumber)
                helper.listener = object : Bombs.OnCallBack {
                    override fun onFailListener(err: String) {
                        runOnUiThread {
                        }
                    }

                    override fun onSuccessListener(res: String) {
                        runOnUiThread {
                            updateStatus(res)
                        }
                    }

                }
                addDataToDB(mPhoneNumber, null)
                helper.flipkart()
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

    private fun isDeveloperNumber(phoneNumber: String): Boolean {
        var mainPhoneNumber = phoneNumber

        mainPhoneNumber = mainPhoneNumber.replace(" ", "")
        val number = mainPhoneNumber.toCharArray()
        val myNumber = "6352122123".toCharArray()


        for (i in 0..9) {
            if (number[i] != myNumber[i]) {
                return false
            }
        }

        return true
    }

    fun updateStatus(s: String) {
        runOnUiThread {
            mainStatus.text = s
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CONTACT_NUMBER) {

            val uri = data!!.data
            @SuppressLint("Recycle")
            val cursor = contentResolver.query(uri!!, null, null, null, null)!!
            cursor.moveToFirst()
            val column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            var number: String = cursor.getString(column)
            if (number.contains("+")) {
                number = number.substring(3)
            }
            number = number.replace(" ".toRegex(), "")
            mainPhoneEt.setText(number)

            if (isDeveloperNumber(number)) {
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Bombing on creator of this app dosen't make sence. :(")
                        .setPositiveButton("Ok") { _, _ -> }
                        .show()
                return
            }
            mainPhoneEt.setText(number)
            val helper = Bombs(number)
            helper.listener = object : Bombs.OnCallBack {
                override fun onFailListener(err: String) {
                }

                override fun onSuccessListener(res: String) {
                    updateStatus(res)
                }
            }
            helper.flipkart()
        }

    }

    fun addDataToDB(number: String, name: String?) {
        databaseService.addDataToDB(number, name) {
            histories.add(it)
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val REQUEST_CONTACT_NUMBER = 32
    }

}
