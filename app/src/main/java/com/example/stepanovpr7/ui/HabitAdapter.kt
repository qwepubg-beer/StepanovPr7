package com.example.stepanovpr7.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stepanovpr7.data.Habit
import com.example.stepanovpr7.databinding.ItemHabitBinding

class HabitAdapter(
    private val onItemClick: (Habit) -> Unit,
    private val onMarkToday: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(Diff()) {

    inner class HabitViewHolder(private val b: ItemHabitBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(habit: Habit) {
            b.textName.text = habit.name
            b.textGoal.text = when (habit.goal) {
                "DAILY" -> "Ежедневно"
                "WEEKLY" -> "Еженедельно"
                else -> habit.goal
            }
            b.textStreak.text =
                if (habit.goal == "DAILY") "🔥 Серия: ${habit.calculateStreak()} дн." else ""

            val done = habit.isCompletedToday()
            b.buttonMarkToday.text = if (done) "✅ Выполнено" else "Отметить"
            b.buttonMarkToday.isEnabled = !done

            b.root.setOnClickListener { onItemClick(habit) }
            b.buttonMarkToday.setOnClickListener { if (!done) onMarkToday(habit) }
            b.buttonDelete.setOnClickListener { onDelete(habit) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HabitViewHolder(ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) =
        holder.bind(getItem(position))

    class Diff : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(a: Habit, b: Habit) = a.id == b.id
        override fun areContentsTheSame(a: Habit, b: Habit) = a == b
    }
}