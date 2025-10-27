package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.Client
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {
    val clients = db.clientDao().getAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    suspend fun get(id: Long) = db.clientDao().getById(id)
    fun save(client: Client) { viewModelScope.launch { db.clientDao().upsert(client) } }
    fun delete(id: Long) { viewModelScope.launch { db.clientDao().delete(id) } }
}
