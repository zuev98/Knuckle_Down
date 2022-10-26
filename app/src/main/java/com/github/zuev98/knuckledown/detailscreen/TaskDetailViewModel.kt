package com.github.zuev98.knuckledown.detailscreen

import androidx.lifecycle.*
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.data.repositories.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(taskId: Int): ViewModel() {
    private val repository = TaskRepository.get()

    private val _task: MutableStateFlow<Task?> = MutableStateFlow(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    init {
        viewModelScope.launch {
            _task.value = repository.getTask(taskId)
        }
    }

    fun updateTaskValue(onUpdate: (Task) -> Task) {
        _task.update { oldTask ->
            oldTask?.let { onUpdate(it) }
        }
    }

    fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            task.value?.let { repository.updateTask(it) }
        }
    }

    suspend fun deleteTask() {
        task.value?.let { repository.deleteTask(it) }
    }
}

class TaskDetailViewModelFactory(
    private val taskId: Int
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskDetailViewModel(taskId) as T
    }
}