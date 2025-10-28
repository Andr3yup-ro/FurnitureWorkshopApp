package com.predandrei.atelier.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
// removed unused imports

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

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

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
    val categoryId: Long?,
    val quantity: Int,
    val priceRon: Long = 0,
    val partNumber: String?,
    val description: String?,
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

@Entity
data class ProjectMaterialUsage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val inventoryItemId: Long,
    val quantityUsed: Int,
    val date: String // ISO date
)
