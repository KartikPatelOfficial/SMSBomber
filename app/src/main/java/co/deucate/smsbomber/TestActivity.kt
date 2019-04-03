package co.deucate.smsbomber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import co.deucate.smsbomber.core.DatabaseHalper
import co.deucate.smsbomber.data.HistoryDatabase


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(applicationContext, HistoryDatabase::class.java, DatabaseHalper.DATABASE_NAME).allowMainThreadQueries().build()
        val data = db.historyDao().getAll()
        Log.d("----->", "Hello\n$data")

    }

}
