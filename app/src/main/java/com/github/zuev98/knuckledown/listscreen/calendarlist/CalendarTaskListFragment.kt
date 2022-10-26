package com.github.zuev98.knuckledown.listscreen.calendarlist

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.databinding.FragmentCalendarTaskListBinding
import com.github.zuev98.knuckledown.dialogs.DatePickerFragment
import com.github.zuev98.knuckledown.listscreen.TaskListViewModel
import com.github.zuev98.knuckledown.listscreen.adapter.TaskListAdapter
import com.github.zuev98.knuckledown.util.Constants.dayInMs
import kotlinx.coroutines.launch
import java.util.*

class CalendarTaskListFragment: Fragment() {
    private var _binding: FragmentCalendarTaskListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val taskListViewModel: TaskListViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskListViewModel.loadDate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarTaskListBinding.inflate(inflater, container, false)

        adapter = TaskListAdapter()
        adapter.setOnItemClickListener {
            showTaskDetail(it)
        }

        bindView()

        return binding.root
    }

    private fun bindView() {
        binding.apply {
            recyclerView.taskRw.adapter = adapter
            recyclerView.taskRw.layoutManager = LinearLayoutManager(context)

            tasksDateBtn.setOnClickListener {
                findNavController().navigate(
                    CalendarTaskListFragmentDirections
                        .selectDate(taskListViewModel.calendarDate.value as Date)
                )
            }
            tasksDateBtn.text = taskListViewModel.getFormattedDateString()

            fab.addFab.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val task = Task(0, date = Date(Date().time + 2 * dayInMs))
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

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE, Date::class.java)!!
                } else {
                    @Suppress("DEPRECATION")
                    bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                }
            taskListViewModel.calendarDate.value = newDate
            updateUi()
        }
    }

    private fun showTaskDetail(taskId: Int) {
        findNavController().navigate(
            CalendarTaskListFragmentDirections
                .showTaskDetail(taskId)
        )
    }

    private fun updateUi() {
        binding.tasksDateBtn.text = taskListViewModel.getFormattedDateString()
        taskListViewModel.setFormattedDate()
        taskListViewModel.loadDate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}