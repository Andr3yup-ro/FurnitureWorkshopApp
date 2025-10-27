package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.InventoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {
    val items = db.inventoryDao().getAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    suspend fun get(id: Long) = db.inventoryDao().getById(id)
    fun save(item: InventoryItem) { viewModelScope.launch { db.inventoryDao().upsert(item) } }
    fun delete(id: Long) { viewModelScope.launch { db.inventoryDao().delete(id) } }
}
