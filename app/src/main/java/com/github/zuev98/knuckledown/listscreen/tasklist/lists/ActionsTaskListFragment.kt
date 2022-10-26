package com.github.zuev98.knuckledown.listscreen.tasklist.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.listscreen.adapter.TaskListAdapter
import com.github.zuev98.knuckledown.listscreen.tasklist.TaskListFragment
import kotlinx.coroutines.launch

class ActionsTaskListFragment: TaskListFragment() {
    override var adapter: TaskListAdapter = TaskListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  super.onCreateView(inflater, container, savedInstanceState)
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
                    val task = Task(0)
                    taskListViewModel.addTask(task)
                    val id = taskListViewModel.getLastId()
                    showTaskDetail(id)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskListViewModel.tasksActions.collect { tasks ->
                    adapter.submitList(tasks)
                }
            }
        }
    }

    override fun showTaskDetail(taskId: Int) {
        findNavController().navigate(
            ActionsTaskListFragmentDirections
                .showTaskDetail(taskId)
        )
    }
}