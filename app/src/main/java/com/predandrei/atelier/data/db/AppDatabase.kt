package com.predandrei.atelier.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.predandrei.atelier.data.model.*

@Database(
    entities = [
        Client::class,
        Project::class,
        Supplier::class,
        InventoryItem::class,
        FinancialTransaction::class,
        PaymentPlan::class,
        Installment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun projectDao(): ProjectDao
    abstract fun supplierDao(): SupplierDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun financeDao(): FinanceDao
    abstract fun paymentDao(): PaymentDao
}
