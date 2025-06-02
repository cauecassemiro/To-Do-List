package com.example.aula13

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aula13.R
import com.example.aula13.Task

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: TaskDatabaseHelper
    private lateinit var editTextTaskInput: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var tasksContainerLayout: LinearLayout

    private val tasksList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTaskInput = findViewById(R.id.editTextTaskInput)
        buttonAddTask = findViewById(R.id.buttonAddTask)
        tasksContainerLayout = findViewById(R.id.tasksContainerLayout)

        dbHelper = TaskDatabaseHelper(this)
        loadTasksFromDatabase()

        buttonAddTask.setOnClickListener {
            val taskDescription = editTextTaskInput.text.toString().trim()
            if (taskDescription.isNotEmpty()) {
                val newTaskId = dbHelper.insertTask(taskDescription)
                if (newTaskId != -1L) {
                    val newTask = Task(id = newTaskId.toInt(), description = taskDescription)
                    addTaskToView(newTask)
                    editTextTaskInput.text.clear()
                } else {
                    Toast.makeText(this, "Erro ao adicionar tarefa!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "A descrição não pode ser vazia!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTasksFromDatabase() {
        val tasks = dbHelper.getAllTasks()
        tasks.forEach { task ->
            addTaskToView(task)
        }
    }

    private fun addTaskToView(task: Task) {
        val taskView = LayoutInflater.from(this).inflate(R.layout.item_todo, tasksContainerLayout, false)
        val textViewDescription = taskView.findViewById<TextView>(R.id.textViewItemDescription)
        val checkBoxCompleted = taskView.findViewById<CheckBox>(R.id.checkBox3)
        val deleteButton = taskView.findViewById<ImageButton>(R.id.imageButton)

        textViewDescription.text = task.description
        checkBoxCompleted.isChecked = task.isCompleted

        fun updateTextStyle() {
            if (checkBoxCompleted.isChecked) {
                textViewDescription.paintFlags = textViewDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textViewDescription.paintFlags = textViewDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        updateTextStyle()

        checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            task.id?.let { dbHelper.updateTaskCompleted(it, isChecked) }
            updateTextStyle()
        }

        deleteButton.setOnClickListener {
            task.id?.let {
                dbHelper.deleteTask(it)
                tasksContainerLayout.removeView(taskView)
                Toast.makeText(this, "Tarefa removida", Toast.LENGTH_SHORT).show()
            }
        }

        tasksContainerLayout.addView(taskView)
    }
}