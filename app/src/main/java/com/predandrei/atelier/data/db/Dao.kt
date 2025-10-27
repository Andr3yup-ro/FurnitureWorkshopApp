package com.predandrei.atelier.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.predandrei.atelier.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM Client ORDER BY name")
    fun getAll(): Flow<List<Client>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg clients: Client)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM Project ORDER BY id DESC")
    fun getAll(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg projects: Project)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM InventoryItem ORDER BY name")
    fun getAll(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: InventoryItem)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM Supplier ORDER BY name")
    fun getAll(): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg suppliers: Supplier)
}

@Dao
interface FinanceDao {
    @Query("SELECT * FROM FinancialTransaction ORDER BY date DESC")
    fun getAll(): Flow<List<FinancialTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg tx: FinancialTransaction)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM PaymentPlan")
    fun getPlans(): Flow<List<PaymentPlan>>

    @Query("SELECT * FROM Installment WHERE planId = :planId")
    fun getInstallments(planId: Long): Flow<List<Installment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlans(vararg plans: PaymentPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertInstallments(vararg inst: Installment)
}
