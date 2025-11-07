package com.predandrei.atelier.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.predandrei.atelier.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "atelier.db")
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed demo data to match web app sample
                    val today = java.time.LocalDate.now()
                    val prevMonth = today.withDayOfMonth(1).minusMonths(1).plusDays(14)
                    val todayStr = today.toString()
                    val prevMonthStr = prevMonth.toString()

                    // Clients
                    db.execSQL("INSERT INTO Client(id, name, phone, email, address) VALUES (1, 'Maria Popescu', NULL, NULL, NULL)")

                    // Project
                    db.execSQL("INSERT INTO Project(id, clientId, title, description, status, valueRon, deadline) VALUES (1, 1, 'Living Room Set - Popescu Family', 'Custom sofa, coffee table, and entertainment unit', 'IN_PROGRESS', 1550000, '2024-07-14')")

                    // Payment plan and one unpaid installment (overdue)
                    db.execSQL("INSERT INTO PaymentPlan(id, projectId, totalRon, advanceRon) VALUES (1, 1, 1550000, 775000)")
                    val overdueDate = today.minusDays(1).toString()
                    db.execSQL("INSERT INTO Installment(id, planId, dueDate, amountRon, paid, method) VALUES (1, 1, '" + overdueDate + "', 775000, 0, 'CASH')")

                    // Inventory items (2 real + 92 dummy to reach 94 total)
                    db.execSQL("INSERT INTO InventoryItem(id, name, categoryId, quantity, priceRon, partNumber, description, minStock, supplierId) VALUES (1, 'Oak Wood Chair', NULL, 20, 45000, NULL, NULL, 2, NULL)")
                    db.execSQL("INSERT INTO InventoryItem(id, name, categoryId, quantity, priceRon, partNumber, description, minStock, supplierId) VALUES (2, 'Glass Coffee Table', NULL, 5, 89000, NULL, NULL, 1, NULL)")

                    // One low-stock dummy item to show alert
                    db.execSQL("INSERT INTO InventoryItem(id, name, categoryId, quantity, priceRon, partNumber, description, minStock, supplierId) VALUES (3, 'Dummy Item 1', NULL, 0, 1000, NULL, NULL, 1, NULL)")
                    var id = 4
                    while (id <= 94) {
                        db.execSQL("INSERT INTO InventoryItem(id, name, categoryId, quantity, priceRon, partNumber, description, minStock, supplierId) VALUES (" + id + ", 'Dummy Item " + (id - 2) + "', NULL, 10, 1000, NULL, NULL, 1, NULL)")
                        id++
                    }

                    // Materials usage allocated to project
                    db.execSQL("INSERT INTO ProjectMaterialUsage(id, projectId, inventoryItemId, quantityUsed, date) VALUES (1, 1, 1, 4, '2024-07-14')")
                    db.execSQL("INSERT INTO ProjectMaterialUsage(id, projectId, inventoryItemId, quantityUsed, date) VALUES (2, 1, 2, 1, '2024-07-14')")

                    // Labor entries
                    db.execSQL("INSERT INTO LaborEntry(id, projectId, date, minutes, hourlyRateRon) VALUES (1, 1, '2024-07-14', 480, 5000)")
                    db.execSQL("INSERT INTO LaborEntry(id, projectId, date, minutes, hourlyRateRon) VALUES (2, 1, '2024-07-14', 720, 4500)")

                    // Finance transactions (this month and previous month revenue, plus cost breakdown)
                    db.execSQL("INSERT INTO FinancialTransaction(id, projectId, type, category, amountRon, date) VALUES (1, 1, 'REVENUE', 'Project Payment', 1550000, '" + todayStr + "')")
                    db.execSQL("INSERT INTO FinancialTransaction(id, projectId, type, category, amountRon, date) VALUES (2, NULL, 'REVENUE', 'Prev Month Revenue', 1383000, '" + prevMonthStr + "')")
                    db.execSQL("INSERT INTO FinancialTransaction(id, projectId, type, category, amountRon, date) VALUES (3, 1, 'EXPENSE', 'Materials', 269000, '2024-07-14')")
                    db.execSQL("INSERT INTO FinancialTransaction(id, projectId, type, category, amountRon, date) VALUES (4, 1, 'EXPENSE', 'Labor', 94000, '2024-07-14')")
                    db.execSQL("INSERT INTO FinancialTransaction(id, projectId, type, category, amountRon, date) VALUES (5, 1, 'EXPENSE', 'Overhead', 150000, '2024-07-14')")
                }
            })
            .build()
}
