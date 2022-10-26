package com.github.zuev98.knuckledown.detailscreen

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.zuev98.knuckledown.R
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.databinding.FragmentTaskDetailBinding
import com.github.zuev98.knuckledown.dialogs.DatePickerFragment
import com.github.zuev98.knuckledown.dialogs.TimePickerFragment
import com.github.zuev98.knuckledown.util.Constants.dayInMs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailFragment: Fragment(), MenuProvider, AdapterView.OnItemSelectedListener {
    private val dateFormat: DateFormat =
        SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("Ru"))
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val args: TaskDetailFragmentArgs by navArgs()
    private val taskDetailViewModel: TaskDetailViewModel by viewModels {
        TaskDetailViewModelFactory(args.taskId)
    }
    private lateinit var listsArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container,false)

        bindView()
        listsArray = resources.getStringArray(R.array.lists_array)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    private fun bindView() {
        binding.apply {
            spinner.onItemSelectedListener = this@TaskDetailFragment
            context?.let {
                ArrayAdapter
                    .createFromResource(
                        it, R.array.lists_array, R.layout.spinner_item
                    ).also { adapter ->
                        adapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter
                    }
            }

            taskEdit.doOnTextChanged { text, _, _, _ ->
                taskDetailViewModel.updateTaskValue { it.copy(desc = text.toString()) }
                if (taskEdit.text.isNullOrBlank())
                    textInputLayout.hint = getString(R.string.enter_task)
                else
                    textInputLayout.hint = getString(R.string.task)
            }

            isDone.setOnCheckedChangeListener { _, isChecked ->
                taskDetailViewModel.updateTaskValue { it.copy(isDone = isChecked) }
            }

            saveFab.setOnClickListener {
                if (!taskEdit.text.isNullOrBlank()) {
                    taskDetailViewModel.updateTask()
                    showSnackbar(getString(R.string.saved),
                        resources.getColor(R.color.green_matte, null))
                    findNavController().popBackStack()
                } else {
                    showSnackbar(getString(R.string.enter_task_name), Color.YELLOW)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskDetailViewModel.task.collect { task ->
                    task?.let { updateUi(it) }
                }
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
            taskDetailViewModel.updateTaskValue { it.copy(date = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { _, bundle ->
            val newTime =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME, Date::class.java)!!
                } else {
                    @Suppress("DEPRECATION")
                    bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
                }
            taskDetailViewModel.updateTaskValue { it.copy(date = newTime) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.taskEdit.text.isNullOrBlank()) {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.alert_delete_task)
                        .setPositiveButton(R.string.alert_confirm) { _, _ ->
                            deleteTask()
                        }
                        .setNeutralButton(R.string.alert_cancel, null)
                        .create()
                        .show()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(task: Task) {
        binding.apply {
            if(taskEdit.text.toString() != task.desc)
                taskEdit.setText(task.desc)

            spinner.setSelection(getSelectionIndex(task.date, task.isInbox))

            dateButton.text = task.date?.let { dateFormat.format(it) }
            dateButton.setOnClickListener {
                findNavController().navigate(
                    TaskDetailFragmentDirections.selectDate(task.date!!)
                )
            }

            timeButton.text = task.date?.let {
                DateUtils.formatDateTime(context, it.time, DateUtils.FORMAT_SHOW_TIME)
            }
            timeButton.setOnClickListener {
                findNavController().navigate(
                    TaskDetailFragmentDirections.selectTime(task.date!!)
                )
            }

            isDone.apply {
                isChecked = task.isDone
                jumpDrawablesToCurrentState()
            }
        }
    }

    private fun getSelectionIndex(taskDate: Date?, taskIsInbox: Boolean): Int {
        if (taskDate != null) {
            val date = dateFormat.parse(dateFormat.format(Date())) as Date
            val taskDay = dateFormat.parse(dateFormat.format(taskDate)) as Date

            return if (taskDay == date) {
                listsArray.indexOf(getString(R.string.menu_today))
            } else if (taskDay.time == (date.time + dayInMs)) {
                listsArray.indexOf(getString(R.string.menu_tomorrow))
            } else
                listsArray.indexOf(getString(R.string.menu_calendar))
        }

        if (!taskIsInbox) {
            return listsArray.indexOf(getString(R.string.menu_actions))
        }

        return listsArray.indexOf(getString(R.string.inbox))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var date: Date? = taskDetailViewModel.task.value?.date
        when (listsArray[position]) {
            getString(R.string.inbox) -> {
                setVisibility(dateBntVisibility = false, timeBtnVisibility = false)
                taskDetailViewModel.updateTaskValue { it.copy(isInbox = true, date = null) }
            }
            getString(R.string.menu_today) -> {
                val today = dateFormat.format(Date())
                setVisibility(dateBntVisibility = false, timeBtnVisibility = true)
                if (date == null || dateFormat.format(date) != today)
                    date = updateTaskDate(Date())
                binding.timeButton.text =
                    DateUtils.formatDateTime(context, date.time, DateUtils.FORMAT_SHOW_TIME)
            }
            getString(R.string.menu_tomorrow) -> {
                val tomorrow = dateFormat.format(Date(Date().time + dayInMs))
                setVisibility(dateBntVisibility = false, timeBtnVisibility = true)
                if (date == null || dateFormat.format(date) != tomorrow)
                    date = updateTaskDate(Date(Date().time + dayInMs))
                binding.timeButton.text =
                    DateUtils.formatDateTime(context, date.time, DateUtils.FORMAT_SHOW_TIME)
            }
            getString(R.string.menu_calendar) -> {
                setVisibility(dateBntVisibility = true, timeBtnVisibility = true)
                if (date == null)
                    date = updateTaskDate(Date(Date().time + 2 * dayInMs))
                binding.dateButton.text = dateFormat.format(date)
                binding.timeButton.text =
                    DateUtils.formatDateTime(context, date.time, DateUtils.FORMAT_SHOW_TIME)
            }
            getString(R.string.menu_actions) -> {
                setVisibility(dateBntVisibility = false, timeBtnVisibility = false)
                taskDetailViewModel.updateTaskValue { it.copy(isInbox = false, date = null) }
            }
        }
    }

    private fun setVisibility(dateBntVisibility: Boolean, timeBtnVisibility: Boolean) {
        binding.dateButtonLabel.visibility = if (dateBntVisibility) View.VISIBLE else View.GONE
        binding.dateButton.visibility = if (dateBntVisibility) View.VISIBLE else View.GONE

        binding.timeButtonLabel.visibility = if (timeBtnVisibility) View.VISIBLE else View.GONE
        binding.timeButton.visibility = if (timeBtnVisibility) View.VISIBLE else View.GONE
    }

    private fun updateTaskDate(date: Date): Date {
        taskDetailViewModel.updateTaskValue { it.copy(isInbox = false, date = date) }
        return taskDetailViewModel.task.value?.date!!
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {  }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_task_detail, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.delete_task -> {
                deleteTask()
                true
            }
            R.id.launch_pomodoro -> {
                findNavController().navigate(
                    TaskDetailFragmentDirections.launchPomodoro()
                )
                true
            }
            else -> false
        }
    }

    private fun deleteTask() {
        viewLifecycleOwner.lifecycleScope.launch {
            taskDetailViewModel.deleteTask()
            showSnackbar(getString(R.string.deleted), Color.RED)
            findNavController().popBackStack()
        }
    }

    private fun showSnackbar(message: String, color: Int) {
        val snackbar = Snackbar.make(binding.parentView, message, Snackbar.LENGTH_LONG)
        val sbTextView = snackbar.view
            .findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        sbTextView.apply {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(color)
        }

        snackbar.show()
    }
}