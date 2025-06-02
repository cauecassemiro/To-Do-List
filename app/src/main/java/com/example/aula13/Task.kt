package com.example.aula13

data class Task(
    val id: Int? = null,
    var description: String,
    var isCompleted: Boolean = false
)