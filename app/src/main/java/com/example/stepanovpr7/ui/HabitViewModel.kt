package com.example.stepanovpr7.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.stepanovpr7.data.Habit
import com.example.stepanovpr7.data.HabitDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = HabitDatabase.getInstance(application).habitDao()

    val allHabits: LiveData<List<Habit>> = dao.getAllHabits()

    fun insert(habit: Habit) = viewModelScope.launch(Dispatchers.IO) { dao.insert(habit) }
    fun update(habit: Habit) = viewModelScope.launch(Dispatchers.IO) { dao.update(habit) }
    fun deleteHabit(habit: Habit) = viewModelScope.launch(Dispatchers.IO) { dao.delete(habit) }

    fun markToday(habit: Habit) {
        val today = Habit.todayString()
        if (!habit.getCompletedDatesList().contains(today)) {
            update(habit.markCompleted(today))
        }
    }

    suspend fun isNameUnique(name: String, excludeId: Long = 0): Boolean =
        dao.countByName(name, excludeId) == 0
}