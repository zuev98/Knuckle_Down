package com.github.zuev98.knuckledown.listscreen.tasklist.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.listscreen.adapter.TaskListAdapter
import com.github.zuev98.knuckledown.listscreen.tasklist.TaskListFragment
import kotlinx.coroutines.launch
import java.util.*

class TodayTaskListFragment: TaskListFragment() {
    override var adapter: TaskListAdapter = TaskListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val date = dateFormat.parse(dateFormat.format(Date())) as Date
        taskListViewModel.loadDate(date)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        adapter.setOnItemClickListener {
            showTaskDetail(it)
        }
        bindView()

        return view
    }

    override fun bindView() {
        binding.apply {
            recyclerView.taskRw.adapter = adapter

            fab.addFab.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val task = Task(id = 0, date = Date())
                    taskListViewModel.addTask(task)
                    val id = taskListViewModel.getLastId()
                    showTaskDetail(id)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskListViewModel.tasksByDate.observe(
            viewLifecycleOwner
        ) { tasks ->
            tasks?.let {
                adapter.submitList(it)
            }
        }
    }

    override fun showTaskDetail(taskId: Int) {
        findNavController().navigate(
            TodayTaskListFragmentDirections
                .showTaskDetail(taskId)
        )
    }
}