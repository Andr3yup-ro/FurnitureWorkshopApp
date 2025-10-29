package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.LaborEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaborViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {
    fun entries(projectId: Long) = db.laborDao().getByProject(projectId)

    fun totalMinutes(projectId: Long): StateFlow<Int> = entries(projectId)
        .map { it.sumOf { e -> e.minutes } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun totalCostRon(projectId: Long): StateFlow<Long> = entries(projectId)
        .map { it.sumOf { e -> (e.hourlyRateRon * e.minutes) / 60 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun save(entry: LaborEntry) { viewModelScope.launch { db.laborDao().upsert(entry) } }
    fun delete(id: Long) { viewModelScope.launch { db.laborDao().delete(id) } }
}
