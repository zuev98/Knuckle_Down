package com.github.zuev98.knuckledown.listscreen

import androidx.lifecycle.*
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.data.repositories.TaskRepository
import com.github.zuev98.knuckledown.util.Constants.dayInMs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TaskListViewModel: ViewModel() {
    private val repository = TaskRepository.get()

    private val _tasksInbox: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val tasksInbox: StateFlow<List<Task>>
        get() = _tasksInbox.asStateFlow()

    private val _tasksActions: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val tasksActions: StateFlow<List<Task>>
        get() = _tasksActions.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getInboxTasks().collect {
                _tasksInbox.value = it
            }
        }

        viewModelScope.launch {
            repository.getActionTasks().collect {
                _tasksActions.value = it
            }
        }
    }

    private val dateFormat: DateFormat =
        SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("Ru"))
    val calendarDate = MutableLiveData<Date>()

    init {
        calendarDate.value = Date(Date().time + 2 * dayInMs)
        setFormattedDate()
    }

    private val taskDateLiveData = MutableLiveData<Date>()

    var tasksByDate: LiveData<List<Task>?> =
        Transformations.switchMap(taskDateLiveData) { taskDate ->
            repository.getTasksByDate(taskDate, Date(taskDate.time + dayInMs))
        }

    fun loadDate() {
        taskDateLiveData.value = calendarDate.value
    }

    fun loadDate(date: Date) {
        taskDateLiveData.value = date
    }

    suspend fun getLastId(): Int {
        return repository.getLastId()
    }

    suspend fun addTask(task: Task) {
        repository.addTask(task)
    }

    fun getFormattedDateString(): String = dateFormat.format(calendarDate.value as Date)

    fun setFormattedDate() {
        calendarDate.value = dateFormat.parse(dateFormat.format(calendarDate.value as Date))
    }
}