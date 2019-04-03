package co.deucate.smsbomber.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import co.deucate.smsbomber.model.History

@Dao
interface HistoryDao {

    @Query("SELECT * FROM History")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(vararg histories: History)

    @Delete
    fun deleteHistory(history: History)
}