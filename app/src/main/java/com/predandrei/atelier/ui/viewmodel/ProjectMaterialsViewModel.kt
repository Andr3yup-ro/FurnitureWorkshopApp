package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.ProjectMaterialUsage
import com.predandrei.atelier.data.repo.MaterialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectMaterialsViewModel @Inject constructor(
    private val db: AppDatabase,
    private val repo: MaterialsRepository
) : ViewModel() {
    fun usage(projectId: Long): StateFlow<List<ProjectMaterialUsage>> =
        db.projectMaterialsDao().getByProject(projectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addUsage(u: ProjectMaterialUsage) {
        viewModelScope.launch { repo.addUsageAndDecrementInventory(u) }
    }
}
