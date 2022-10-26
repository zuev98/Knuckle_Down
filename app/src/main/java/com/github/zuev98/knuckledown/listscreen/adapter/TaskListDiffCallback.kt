package com.github.zuev98.knuckledown.listscreen.adapter

import androidx.recyclerview.widget.DiffUtil
import com.github.zuev98.knuckledown.data.entities.Task

class TaskListDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}