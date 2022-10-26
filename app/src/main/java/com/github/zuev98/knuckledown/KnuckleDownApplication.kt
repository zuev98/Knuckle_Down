package com.github.zuev98.knuckledown

import android.app.Application
import com.github.zuev98.knuckledown.data.repositories.TaskRepository

class KnuckleDownApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        TaskRepository.initialize(this)
    }
}