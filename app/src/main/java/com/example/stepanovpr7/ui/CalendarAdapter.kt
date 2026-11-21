package com.example.stepanovpr7.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stepanovpr7.R
import com.example.stepanovpr7.data.Habit
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private var habit: Habit,
    private val onDateClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayVH>() {

    private var days: List<Date?> = emptyList()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dayFmt = SimpleDateFormat("d", Locale.getDefault())

    fun setMonth(cal: Calendar) {
        val list = mutableListOf<Date?>()
        val t = cal.clone() as Calendar
        t.set(Calendar.DAY_OF_MONTH, 1)
        val empty = when (t.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0; Calendar.TUESDAY -> 1; Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3; Calendar.FRIDAY -> 4; Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6; else -> 0
        }
        repeat(empty) { list.add(null) }
        for (d in 1..t.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            t.set(Calendar.DAY_OF_MONTH, d)
            list.add(t.time)
        }
        days = list
        notifyDataSetChanged()
    }

    fun updateHabit(h: Habit) { habit = h; notifyDataSetChanged() }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int) =
        DayVH(LayoutInflater.from(p.context).inflate(R.layout.item_calendar_day, p, false))

    override fun onBindViewHolder(h: DayVH, pos: Int) {
        val date = days[pos] ?: run {
            h.textDay.text = ""; h.itemView.setBackgroundColor(Color.TRANSPARENT)
            h.itemView.setOnClickListener(null); return
        }
        val ds = sdf.format(date)
        h.textDay.text = dayFmt.format(date)
        val completed = habit.getCompletedDatesList().contains(ds)
        val today = Habit.todayString()
        h.itemView.setBackgroundColor(
            when {
                ds == today -> Color.parseColor("#B3E5FC")
                completed -> Color.parseColor("#C8E6C9")
                else -> Color.TRANSPARENT
            }
        )
        h.itemView.setOnClickListener { onDateClick(ds) }
    }

    override fun getItemCount() = days.size

    class DayVH(v: View) : RecyclerView.ViewHolder(v) {
        val textDay: TextView = v.findViewById(R.id.textDay)
    }
}