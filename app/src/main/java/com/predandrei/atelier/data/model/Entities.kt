package com.predandrei.atelier.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

enum class ProjectStatus { PENDING, IN_PROGRESS, COMPLETED, ON_HOLD }

@Entity
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String?,
    val email: String?,
    val address: String?
)

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val title: String,
    val description: String?,
    val status: ProjectStatus = ProjectStatus.PENDING,
    val valueRon: Long = 0, // store as minor units (bani)
    val deadline: String? // ISO date string
)

enum class InventoryCategory { CHAIRS, TABLES, SOFAS, CABINETS, ACCESSORIES }

@Entity
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contact: String?
)

@Entity
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: InventoryCategory,
    val quantity: Int,
    val minStock: Int = 0,
    val supplierId: Long?
)

enum class TransactionType { REVENUE, EXPENSE }

@Entity
data class FinancialTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long?,
    val type: TransactionType,
    val category: String,
    val amountRon: Long, // minor units
    val date: String // ISO date
)

enum class PaymentMethod { CASH, CARD, BANK_TRANSFER }

@Entity
data class PaymentPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val totalRon: Long,
    val advanceRon: Long = 0
)

@Entity
data class Installment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val dueDate: String, // ISO date
    val amountRon: Long,
    val paid: Boolean = false,
    val method: PaymentMethod = PaymentMethod.CASH
)
