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

    @Query("SELECT * FROM Client WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Client?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg clients: Client)

    @Query("DELETE FROM Client WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM Project ORDER BY id DESC")
    fun getAll(): Flow<List<Project>>

    @Query("SELECT * FROM Project WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg projects: Project)

    @Query("DELETE FROM Project WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM InventoryItem ORDER BY name")
    fun getAll(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM InventoryItem WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): InventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg items: InventoryItem)

    @Query("DELETE FROM InventoryItem WHERE id = :id")
    suspend fun delete(id: Long)
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

    @Query("SELECT * FROM PaymentPlan WHERE projectId = :projectId LIMIT 1")
    suspend fun getPlanByProjectId(projectId: Long): PaymentPlan?

    @Query("SELECT * FROM Installment WHERE planId = :planId")
    fun getInstallments(planId: Long): Flow<List<Installment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlans(vararg plans: PaymentPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertInstallments(vararg inst: Installment)
}
