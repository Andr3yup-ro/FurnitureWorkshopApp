package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.Installment
import com.predandrei.atelier.data.model.PaymentMethod
import com.predandrei.atelier.data.model.PaymentPlan
import com.predandrei.atelier.data.model.FinancialTransaction
import com.predandrei.atelier.data.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {

    fun planForProject(projectId: Long): StateFlow<PaymentPlan?> =
        db.paymentDao().getPlans().map { plans -> plans.firstOrNull { it.projectId == projectId } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun installments(planId: Long) = db.paymentDao().getInstallments(planId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun upsertPlan(plan: PaymentPlan) { viewModelScope.launch { db.paymentDao().upsertPlans(plan) } }
    fun upsertInstallments(vararg inst: Installment) { viewModelScope.launch { db.paymentDao().upsertInstallments(*inst) } }

    fun markPaid(inst: Installment, projectId: Long) {
        viewModelScope.launch {
            if (!inst.paid) {
                db.paymentDao().upsertInstallments(inst.copy(paid = true))
                // Record revenue transaction on payment
                db.financeDao().upsert(
                    FinancialTransaction(
                        projectId = projectId,
                        type = TransactionType.REVENUE,
                        category = "INSTALLMENT",
                        amountRon = inst.amountRon,
                        date = java.time.LocalDate.now().toString()
                    )
                )
            }
        }
    }
}
