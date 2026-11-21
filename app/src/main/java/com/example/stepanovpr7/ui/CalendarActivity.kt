package com.example.stepanovpr7.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stepanovpr7.data.Habit
import com.example.stepanovpr7.data.HabitDatabase
import com.example.stepanovpr7.databinding.ActivityCalendarBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var b: ActivityCalendarBinding
    private lateinit var habit: Habit
    private lateinit var adapter: CalendarAdapter
    private val currentMonth: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(b.root)

        val id = intent.getLongExtra("habit_id", -1L)
        if (id == -1L) { finish(); return }

        val dao = HabitDatabase.getInstance(this).habitDao()
        lifecycleScope.launch {
            val h = dao.getHabitById(id) ?: run { finish(); return@launch }
            habit = h
            setup()
        }
    }

    private fun setup() {
        b.textHabitName.text = habit.name
        adapter = CalendarAdapter(habit) { ds ->
            habit = if (habit.getCompletedDatesList().contains(ds)) habit.unmarkCompleted(ds)
            else habit.markCompleted(ds)
            lifecycleScope.launch {
                HabitDatabase.getInstance(this@CalendarActivity).habitDao().update(habit)
            }
            adapter.updateHabit(habit)
        }
        b.recyclerCalendar.layoutManager = GridLayoutManager(this, 7)
        b.recyclerCalendar.adapter = adapter

        b.buttonPrevMonth.setOnClickListener { currentMonth.add(Calendar.MONTH, -1); render() }
        b.buttonNextMonth.setOnClickListener { currentMonth.add(Calendar.MONTH, 1); render() }
        render()
    }

    private fun render() {
        b.textMonthYear.text =
            SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(currentMonth.time)
        adapter.setMonth(currentMonth)
    }
}