package com.github.zuev98.knuckledown.listscreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.zuev98.knuckledown.R
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.databinding.ListItemTaskBinding
import java.util.*

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskHolder>(TaskListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTaskBinding.inflate(inflater, parent, false)
        return TaskHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        val  task = getItem(position)
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class TaskHolder(
        private val binding: ListItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.taskHeading.text = task.desc
            //Mark the background of completed or overdue tasks
            if (task.isDone) {
                binding.taskHeading.setBackgroundColor(
                    ContextCompat.getColor(binding.taskHeading.context, R.color.green_matte)
                )
            } else {
                task.date?.let {
                    if (it < Date()) {
                        binding.taskHeading.setBackgroundColor(
                            ContextCompat.getColor(binding.taskHeading.context, R.color.red_light)
                        )
                    }
                }
            }

            binding.root.setOnClickListener {
                onItemClickListener?.let { it(task.id) }
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }
}

