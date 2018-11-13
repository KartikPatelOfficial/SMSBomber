package co.deucate.smsbomber

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DatabaseHalper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "History.db"

        const val TABLE_NAME = "History"
        const val COLUMN_NAME_NAME = "Name"
        const val COLUMN_NAME_Number = "Number"
        const val COLUMN_NAME_TIME = "Time"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
        private const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY," +
                " $COLUMN_NAME_NAME TEXT," +
                " $COLUMN_NAME_Number TEXT," +
                " $COLUMN_NAME_TIME TEXT)"

    }
}

