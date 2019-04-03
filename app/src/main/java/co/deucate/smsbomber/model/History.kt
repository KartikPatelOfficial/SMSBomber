package co.deucate.smsbomber.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
        @PrimaryKey(autoGenerate = true)
        var index: Int = 0,
        val name: String,
        val number: String,
        val time: String
)