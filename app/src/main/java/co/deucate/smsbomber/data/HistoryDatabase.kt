package co.deucate.smsbomber.data

import androidx.room.Database
import androidx.room.RoomDatabase
import co.deucate.smsbomber.model.History

@Database(entities = [History::class], version = 2, exportSchema = false)
abstract class HistoryDatabase:RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}