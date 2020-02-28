package co.deucate.smsbomber

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import co.deucate.smsbomber.core.DatabaseHelper
import co.deucate.smsbomber.model.History
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DatabaseService(context: Context) {

    private var db: SQLiteDatabase
    private var dbHelper: DatabaseHelper = DatabaseHelper(context)

    init {
        db = dbHelper.readableDatabase
    }

    fun getHistories(completion: (histories: ArrayList<History>) -> Unit) {
        val projection = arrayOf(BaseColumns._ID, DatabaseHelper.COLUMN_NAME_NAME, DatabaseHelper.COLUMN_NAME_Number, DatabaseHelper.COLUMN_NAME_TIME)
        val cursor = db.query(DatabaseHelper.TABLE_NAME, projection, null, null, null, null, null)

        val histories = ArrayList<History>()

        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME_NAME))
                val time = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME_TIME))
                val number = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME_Number))
                histories.add(History(itemId.toInt(), name, number, time))
            }
        }
        completion(histories)
    }

    @SuppressLint("SimpleDateFormat")
    fun addDataToDB(phoneNumber: String, names: String?, completion: (history: History) -> Unit) {
        val db = dbHelper.writableDatabase
        var name = names
        if (names == null) {
            name = "Unknown"
        }

        val calendar = Calendar.getInstance()
        val motorman = SimpleDateFormat("dd/MM/yyyy hh:mm aa")
        val strDate = motorman.format(calendar.time)

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME_NAME, name)
            put(DatabaseHelper.COLUMN_NAME_Number, phoneNumber)
            put(DatabaseHelper.COLUMN_NAME_TIME, strDate)

        }

        val newRowId = db?.insert(DatabaseHelper.TABLE_NAME, null, values)

        completion(History(newRowId!!.toInt(), name!!, phoneNumber, strDate))
    }

    fun deleteFromHistory(id: Int) {
        db.execSQL("DELETE FROM ${DatabaseHelper.TABLE_NAME} WHERE ${BaseColumns._ID}=${id}")
    }

}