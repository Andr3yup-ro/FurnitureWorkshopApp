package com.predandrei.atelier.data.repo

import androidx.room.withTransaction
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.ProjectMaterialUsage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaterialsRepository @Inject constructor(private val db: AppDatabase) {
    suspend fun addUsageAndDecrementInventory(usage: ProjectMaterialUsage) {
        db.withTransaction {
            val item = db.inventoryDao().getById(usage.inventoryItemId) ?: return@withTransaction
            val newQty = (item.quantity - usage.quantityUsed).coerceAtLeast(0)
            db.inventoryDao().upsert(item.copy(quantity = newQty))
            db.projectMaterialsDao().upsert(usage)
        }
    }
}
