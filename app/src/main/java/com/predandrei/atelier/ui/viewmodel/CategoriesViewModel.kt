package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val db: AppDatabase) : ViewModel() {
    val categories = db.categoryDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    suspend fun get(id: Long) = db.categoryDao().getById(id)
    fun save(c: Category) { viewModelScope.launch { db.categoryDao().upsert(c) } }
    fun delete(id: Long) { viewModelScope.launch { db.categoryDao().delete(id) } }
}
