package com.example.stepanovpr7

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val goal: String,
    val createdAt: Long = System.currentTimeMillis(),
    val completedDates: String = ""
) {
    fun getCompletedDatesList(): List<String> {
        if (completedDates.isBlank()) return emptyList()
        return completedDates.split(",").filter { it.isNotBlank() }
    }
    fun markCompleted(date: String): Habit {
        val dates = getCompletedDatesList().toMutableSet()
        dates.add(date)
        return copy(completedDates = dates.joinToString(","))
    }
    fun unmarkCompleted(date: String): Habit {
        val dates = getCompletedDatesList().toMutableSet()
        dates.remove(date)
        return copy(completedDates = dates.joinToString(","))
    }
    fun calculateStreak(): Int {
        val dates = getCompletedDatesList().toSet()
        if (dates.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        var streak = 0
        val todayStr = sdf.format(calendar.time)
        if (!dates.contains(todayStr)) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        while (true) {
            val dateStr = sdf.format(calendar.time)
            if (dates.contains(dateStr)) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
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