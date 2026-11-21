package com.example.stepanovpr7

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stepanovpr7.databinding.ActivityMainBinding
import com.example.stepanovpr7.ui.AddHabitActivity
import com.example.stepanovpr7.ui.CalendarActivity
import com.example.stepanovpr7.ui.HabitAdapter
import com.example.stepanovpr7.ui.HabitViewModel
import com.example.stepanovpr7.util.NotificationUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HabitAdapter
    private lateinit var viewModel: HabitViewModel

    private val addHabitLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Привычка добавлена!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        NotificationUtils.createNotificationChannel(this)

        viewModel = ViewModelProvider(this)[HabitViewModel::class.java]

        adapter = HabitAdapter(
            onItemClick = { habit ->
                startActivity(
                    Intent(this, CalendarActivity::class.java).putExtra("habit_id", habit.id)
                )
            },
            onMarkToday = { viewModel.markToday(it) },
            onDelete = { viewModel.deleteHabit(it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.allHabits.observe(this) { habits ->
            adapter.submitList(habits)
            binding.textEmpty.visibility = if (habits.isEmpty()) android.view.View.VISIBLE
            else android.view.View.GONE
        }

        binding.fabAdd.setOnClickListener {
            addHabitLauncher.launch(Intent(this, AddHabitActivity::class.java))
        }
    }
}