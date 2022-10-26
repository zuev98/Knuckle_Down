package com.github.zuev98.knuckledown.listscreen.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zuev98.knuckledown.databinding.FragmentTaskListBinding
import com.github.zuev98.knuckledown.listscreen.TaskListViewModel
import com.github.zuev98.knuckledown.listscreen.adapter.TaskListAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

abstract class TaskListFragment : Fragment() {
    abstract var adapter: TaskListAdapter
    protected val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("Ru"))

    private var _binding: FragmentTaskListBinding? = null
    protected val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    protected val taskListViewModel: TaskListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        binding.recyclerView.taskRw.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    abstract fun bindView()

    abstract fun showTaskDetail(taskId: Int)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}