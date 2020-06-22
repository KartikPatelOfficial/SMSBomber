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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import co.deucate.smsbomber.core.Bombs
import co.deucate.smsbomber.core.DatabaseService
import co.deucate.smsbomber.model.History
import co.deucate.smsbomber.ui.ProtectedActivity
import co.deucate.smsbomber.ui.settings.SettingsActivity
import co.deucate.smsbomber.utility.Validator
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HomeActivity : AppCompatActivity() {

    private lateinit var databaseService: DatabaseService
    private val histories = ArrayList<History>()

    private lateinit var adapter: Adapter
    private lateinit var mInterstitialAd: InterstitialAd

    companion object {
        private const val REQUEST_CONTACT_NUMBER = 32
    }

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
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.InterstitialID)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        adapter = Adapter(histories) { history ->
            AlertDialog.Builder(this@HomeActivity).setTitle("Warning!!!").setMessage("Are ou sure ou want to start bombing on ${history.number}?")
                    .setPositiveButton("YES") { _, _ ->
                        this@HomeActivity.mainPhoneEt.text = SpannableStringBuilder.valueOf(history.number)
                        startBombing(history.number, history.name)
                        adapter.notifyDataSetChanged()

                    }.setNegativeButton("NO") { _, _ -> }.show()
        }

        databaseService.getHistories {
            histories.clear()
            histories.addAll(it)
            adapter.notifyDataSetChanged()
        }

        mainRecyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
        mainRecyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(mainRecyclerView)

        if (checkNetwork()) return

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
            startBombing(mPhoneNumber)
            return@OnClickListener
        })

        findViewById<View>(R.id.contactBtn).setOnClickListener(View.OnClickListener {
            if (checkNetwork()) return@OnClickListener
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, REQUEST_CONTACT_NUMBER)
        })

    }

    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

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

    private fun checkNetwork(): Boolean {
        if (!isNetworkAvailable) {
            AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please connect to network")
                    .setPositiveButton("Ok") { _, _ -> }
                    .show()
            return true
        }
        return false
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

    private fun startBombing(phoneNumber: String, name: String? = null) {
        val validationError = Validator(phoneNumber).validate()

        if (validationError != null) {
            AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(validationError)
                    .setPositiveButton("Ok") { _, _ -> }
                    .show()
            return
        }
        Bombs(phoneNumber) { isSuccess, message ->
            if (isSuccess) {
                runOnUiThread {
                    updateStatus(message)
                }
            }
        }
        addDataToDB(phoneNumber, name)
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.")
        }
    }

    private fun updateStatus(currentStatus: String) {
        runOnUiThread {
            mainStatus.text = currentStatus
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CONTACT_NUMBER) {

            @SuppressLint("Recycle")
            val cursor = contentResolver.query(data!!.data!!, null, null, null, null)!!
            cursor.moveToFirst()
            val column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val number: String = cursor.getString(column)
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            startBombing(number, name)
        }
    }

    private fun addDataToDB(number: String, name: String?) {
        databaseService.addDataToDB(number, name) {
            histories.add(it)
            adapter.notifyDataSetChanged()
        }
    }
}
