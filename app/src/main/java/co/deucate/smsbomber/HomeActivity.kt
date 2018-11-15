package co.deucate.smsbomber

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import co.deucate.smsbomber.settings.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPhoneEt: EditText

    private lateinit var mStatus: TextView

    internal var currentTime: Date? = null

    private lateinit var dbHelper: DatabaseHalper
    private val history = ArrayList<Data>()

    val adapter = Adapter(history)

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun onResume() {
        super.onResume()

        if (!recreated) {
            recreate()
            recreated = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        isNightModeEnabled = mPrefs.getBoolean("NIGHT_MODE", false)

        if (isNightModeEnabled) {
            setTheme(R.style.DarkMode)
        } else {
            setTheme(R.style.AppTheme)
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.bottomBar))

        dbHelper = DatabaseHalper(this)
        val db = dbHelper.readableDatabase

        getDataBase(db)

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
                val removedItem = history[position]

                db.execSQL("DELETE FROM ${DatabaseHalper.TABLE_NAME} WHERE ${BaseColumns._ID}=${history[position].index}")
                history.removeAt(position)
                adapter.notifyDataSetChanged()

                val sandbar = Snackbar.make(findViewById(R.id.coordinator), "Deleted Successfully", Snackbar.LENGTH_LONG).setAction("UNDO") {
                    addDataToDB(removedItem.number, removedItem.name)
                    adapter.notifyDataSetChanged()
                    val sandbar1 = Snackbar.make(findViewById(R.id.coordinator), "Data is restored!", Snackbar.LENGTH_SHORT)
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
                xMark!!.setBounds(xMarkLeft - 40, xMarkTop + 50, xMarkRight - 50, xMarkBottom - 50)
                xMark!!.draw(c)
            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        mRecyclerView = findViewById(R.id.mainRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
        mRecyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
        mPhoneEt = findViewById(R.id.mainPhoneEt)
        mStatus = findViewById(R.id.mainStatus)

        if (!isNetworkAvailable) {
            AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please connect to network")
                    .setPositiveButton("Ok") { _, _ -> }
                    .show()
            return
        }

        //todo
//        AlertDialog.Builder(this)
//                .setTitle("Attention")
//                .setMessage(getString(R.string.warning))
//                .setPositiveButton("Ok") { _, _ -> }
//                .show()

        findViewById<View>(R.id.mainOkBtn).setOnClickListener(View.OnClickListener {
            val mPhoneNumber = mPhoneEt.text.toString()

            if (TextUtils.isEmpty(mPhoneNumber)) {
                mPhoneEt.error = "Please enter mobile number"
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

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val itemID = item!!.itemId

        when (itemID) {
            R.id.menuSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("NIGHT_MODE", isNightModeEnabled)
                startActivity(intent)
            }
        }

        return true
    }

    private fun getDataBase(db: SQLiteDatabase) {

        val projection = arrayOf(BaseColumns._ID, DatabaseHalper.COLUMN_NAME_NAME, DatabaseHalper.COLUMN_NAME_Number, DatabaseHalper.COLUMN_NAME_TIME)
        val cursor = db.query(DatabaseHalper.TABLE_NAME, projection, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(DatabaseHalper.COLUMN_NAME_NAME))
                val time = getString(getColumnIndexOrThrow(DatabaseHalper.COLUMN_NAME_TIME))
                val number = getString(getColumnIndexOrThrow(DatabaseHalper.COLUMN_NAME_Number))
                history.add(Data(itemId.toString(), name, number, time))
            }
        }
        adapter.notifyDataSetChanged()

    }

    @SuppressLint("SimpleDateFormat")
    private fun isProtectedNumber(mPhoneNumber: String) {

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Protected").document(mPhoneNumber).get().addOnCompleteListener { task ->
            val snapshot = task.result
            if (snapshot!!.exists()) {

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
                        val helper = Bombs(mPhoneNumber)
                        helper.listner = object : Bombs.OnCallBack {
                            override fun onFailListner(err: String) {
                            }

                            override fun onSuccessListner(res: String) {
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
                helper.listner = object : Bombs.OnCallBack {
                    override fun onFailListner(err: String) {
                        runOnUiThread {
                        }
                    }

                    override fun onSuccessListner(res: String) {
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
        val myNumber = "9664769226".toCharArray()


        for (i in 0..9) {
            if (number[i] != myNumber[i]) {
                return false
            }
        }

        return true
    }

    fun updateStatus(s: String) {
        runOnUiThread {
            mStatus.text = s
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CONTACT_NUMBER) {

            val uri = data!!.data
            @SuppressLint("Recycle")
            val cursor = contentResolver.query(uri!!, null, null, null, null)!!
            cursor.moveToFirst()
            val colum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            var number: String = cursor.getString(colum)
            if (number.contains("+")) {
                number = number.substring(3)
            }
            number = number.replace(" ".toRegex(), "")
            mPhoneEt.setText(number)

            if (isDeveloperNumber(number)) {
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Bombing on creator of this app dosen't make sence. :(")
                        .setPositiveButton("Ok") { _, _ -> }
                        .show()
                return
            }
            mPhoneEt.setText(number)
            val helper = Bombs(number)
            helper.listner = object : Bombs.OnCallBack {
                override fun onFailListner(err: String) {
                }

                override fun onSuccessListner(res: String) {
                    updateStatus(res)
                }

            }
            addDataToDB(number, name)
            helper.flipkart()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun addDataToDB(phoneNumber: String, names: String?) {
        val db = dbHelper.writableDatabase
        var name = names
        if (names == null) {
            name = "Unknown"
        }

        val calendar = Calendar.getInstance()
        val motorman = SimpleDateFormat("dd/MM/yyyy hh:mm aa")
        val strDate = motorman.format(calendar.time)

        val values = ContentValues().apply {
            put(DatabaseHalper.COLUMN_NAME_NAME, name)
            put(DatabaseHalper.COLUMN_NAME_Number, phoneNumber)
            put(DatabaseHalper.COLUMN_NAME_TIME, strDate)

        }

        val newRowId = db?.insert(DatabaseHalper.TABLE_NAME, null, values)

        history.add(Data(newRowId.toString(), name!!, phoneNumber, strDate))
        adapter.notifyDataSetChanged()
    }

    companion object {
        var recreated = false
        var isNightModeEnabled: Boolean = false
        private const val REQUEST_CONTACT_NUMBER = 32
    }

}
