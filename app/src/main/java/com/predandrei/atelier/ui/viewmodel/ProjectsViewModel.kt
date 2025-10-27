package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.Project
import com.predandrei.atelier.data.model.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {
    val projects = db.projectDao().getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun get(id: Long) = db.projectDao().getById(id)

    fun save(project: Project) {
        viewModelScope.launch { db.projectDao().upsert(project) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { db.projectDao().delete(id) }
    }
}
