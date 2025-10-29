package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.FinancialTransaction
import com.predandrei.atelier.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {
    val transactions = db.financeDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // For breakdowns
    val inventory = db.inventoryDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val materialUsages = db.projectMaterialsDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val laborEntries = db.laborDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val revenueTotalRon: StateFlow<Long> = transactions.map { list ->
        list.filter { it.type == TransactionType.REVENUE }.sumOf { it.amountRon }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val expenseTotalRon: StateFlow<Long> = transactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountRon }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val profitRon: StateFlow<Long> = combine(revenueTotalRon, expenseTotalRon) { rev, exp -> rev - exp }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun save(tx: FinancialTransaction) {
        viewModelScope.launch { db.financeDao().upsert(tx) }
    }
}
