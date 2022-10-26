package com.github.zuev98.knuckledown.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var desc: String = "",
    var isInbox: Boolean = false,
    var date: Date? = null,
    var isDone: Boolean = false
)
