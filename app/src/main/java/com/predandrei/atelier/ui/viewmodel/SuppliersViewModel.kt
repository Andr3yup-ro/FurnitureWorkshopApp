package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuppliersViewModel @Inject constructor(private val db: AppDatabase) : ViewModel() {
    val suppliers = db.supplierDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    suspend fun get(id: Long) = db.supplierDao().getById(id)
    fun save(s: Supplier) { viewModelScope.launch { db.supplierDao().upsert(s) } }
    fun delete(id: Long) { viewModelScope.launch { db.supplierDao().delete(id) } }
}
