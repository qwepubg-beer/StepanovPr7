package com.example.stepanovpr7.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val goal: String, // "DAILY" или "WEEKLY"
    val createdAt: Long = System.currentTimeMillis(),
    val completedDates: String = ""
) {
    fun getCompletedDatesList(): List<String> =
        if (completedDates.isBlank()) emptyList()
        else completedDates.split(",").filter { it.isNotBlank() }

    fun markCompleted(date: String): Habit {
        val set = getCompletedDatesList().toMutableSet()
        set.add(date)
        return copy(completedDates = set.joinToString(","))
    }

    fun unmarkCompleted(date: String): Habit {
        val set = getCompletedDatesList().toMutableSet()
        set.remove(date)
        return copy(completedDates = set.joinToString(","))
    }

    fun calculateStreak(): Int {
        val dates = getCompletedDatesList().toSet()
        if (dates.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        if (!dates.contains(sdf.format(cal.time))) cal.add(Calendar.DAY_OF_YEAR, -1)
        var streak = 0
        while (true) {
            val s = sdf.format(cal.time)
            if (dates.contains(s)) { streak++; cal.add(Calendar.DAY_OF_YEAR, -1) }
            else break
        }
        return streak
    }

    fun isCompletedToday(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return getCompletedDatesList().contains(today)
    }

    companion object {
        fun todayString(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
