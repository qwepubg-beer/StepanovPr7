package com.example.stepanovpr7.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.stepanovpr7.data.Habit
import com.example.stepanovpr7.databinding.ActivityAddHabitBinding
import kotlinx.coroutines.launch

class AddHabitActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddHabitBinding
    private lateinit var viewModel: HabitViewModel
    private val maxLen = 30
    private var isNameUnique = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddHabitBinding.inflate(layoutInflater)
        setContentView(b.root)

        viewModel = ViewModelProvider(this)[HabitViewModel::class.java]

        b.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, bb: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val len = s?.length ?: 0
                if (len > maxLen) {
                    b.editName.setText(s!!.subSequence(0, maxLen))
                    b.editName.setSelection(maxLen)
                }
                b.textCounter.text = "$len / $maxLen"
            }
            override fun afterTextChanged(s: Editable?) {
                val name = s?.toString()?.trim() ?: ""
                if (name.isEmpty()) { isNameUnique = false; b.textNameError.text = ""; return }
                lifecycleScope.launch {
                    val unique = viewModel.isNameUnique(name)
                    isNameUnique = unique
                    b.textNameError.text = if (unique) "" else "Такое название уже существует"
                }
            }
        })

        b.buttonSave.setOnClickListener { save() }
        b.buttonCancel.setOnClickListener { setResult(RESULT_CANCELED); finish() }
    }

    private fun save() {
        val name = b.editName.text.toString().trim()
        val goal = if (b.radioWeekly.isChecked) "WEEKLY" else "DAILY"

        when {
            name.isEmpty() -> { b.textNameError.text = "Введите название"; return }
            name.length > maxLen -> { b.textNameError.text = "Не более $maxLen символов"; return }
            !isNameUnique -> { b.textNameError.text = "Название должно быть уникальным"; return }
        }

        viewModel.insert(Habit(name = name, goal = goal))
        setResult(RESULT_OK)
        Toast.makeText(this, "Привычка «$name» добавлена", Toast.LENGTH_SHORT).show()
        finish()
    }
}