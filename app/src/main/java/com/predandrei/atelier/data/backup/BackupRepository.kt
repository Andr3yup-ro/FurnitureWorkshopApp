package com.predandrei.atelier.data.backup

import android.content.Context
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.data.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase
) {
    private fun file(): File = File(context.getExternalFilesDir(null), "backup.json")

    suspend fun backup(): File {
        val root = JSONObject()
        root.put("clients", JSONArray(db.clientDao().getAll().first().map { it.toJson() }))
        root.put("projects", JSONArray(db.projectDao().getAll().first().map { it.toJson() }))
        root.put("categories", JSONArray(db.categoryDao().getAll().first().map { it.toJson() }))
        root.put("suppliers", JSONArray(db.supplierDao().getAll().first().map { it.toJson() }))
        root.put("inventory", JSONArray(db.inventoryDao().getAll().first().map { it.toJson() }))
        root.put("finance", JSONArray(db.financeDao().getAll().first().map { it.toJson() }))
        root.put("payments", JSONArray(db.paymentDao().getPlans().first().map { it.toJson() }))
        root.put("installments", JSONArray(db.paymentDao().getAllInstallments().first().map { it.toJson() }))
        root.put("materialsUsage", JSONArray(db.projectMaterialsDao().getAll().first().map { it.toJson() }))

        val f = file()
        f.writeText(root.toString(), Charset.forName("UTF-8"))
        return f
    }

    suspend fun restore(): File? {
        val f = file()
        if (!f.exists()) return null
        val text = f.readText(Charset.forName("UTF-8"))
        val root = JSONObject(text)

        // Upsert in a safe order
        root.optJSONArray("clients")?.let { arr ->
            for (i in 0 until arr.length()) db.clientDao().upsert(arr.getJSONObject(i).toClient())
        }
        root.optJSONArray("categories")?.let { arr ->
            for (i in 0 until arr.length()) db.categoryDao().upsert(arr.getJSONObject(i).toCategory())
        }
        root.optJSONArray("suppliers")?.let { arr ->
            for (i in 0 until arr.length()) db.supplierDao().upsert(arr.getJSONObject(i).toSupplier())
        }
        root.optJSONArray("inventory")?.let { arr ->
            for (i in 0 until arr.length()) db.inventoryDao().upsert(arr.getJSONObject(i).toInventoryItem())
        }
        root.optJSONArray("projects")?.let { arr ->
            for (i in 0 until arr.length()) db.projectDao().upsert(arr.getJSONObject(i).toProject())
        }
        root.optJSONArray("payments")?.let { arr ->
            for (i in 0 until arr.length()) db.paymentDao().upsertPlans(arr.getJSONObject(i).toPaymentPlan())
        }
        root.optJSONArray("installments")?.let { arr ->
            for (i in 0 until arr.length()) db.paymentDao().upsertInstallments(arr.getJSONObject(i).toInstallment())
        }
        root.optJSONArray("finance")?.let { arr ->
            for (i in 0 until arr.length()) db.financeDao().upsert(arr.getJSONObject(i).toFinancialTransaction())
        }
        root.optJSONArray("materialsUsage")?.let { arr ->
            for (i in 0 until arr.length()) db.projectMaterialsDao().upsert(arr.getJSONObject(i).toProjectMaterialUsage())
        }
        return f
    }
}

private fun Client.toJson() = JSONObject().apply {
    put("id", id); put("name", name); put("phone", phone); put("email", email); put("address", address)
}
private fun JSONObject.toClient() = Client(
    id = optLong("id"), name = getString("name"), phone = optString("phone").takeIf { it.isNotBlank() },
    email = optString("email").takeIf { it.isNotBlank() }, address = optString("address").takeIf { it.isNotBlank() }
)

private fun Project.toJson() = JSONObject().apply {
    put("id", id); put("clientId", clientId); put("title", title); put("description", description)
    put("status", status.name); put("valueRon", valueRon); put("deadline", deadline)
}
private fun JSONObject.toProject() = Project(
    id = optLong("id"), clientId = optLong("clientId"), title = getString("title"),
    description = optString("description").takeIf { it.isNotBlank() },
    status = ProjectStatus.valueOf(getString("status")), valueRon = optLong("valueRon"),
    deadline = optString("deadline").takeIf { it.isNotBlank() }
)

private fun InventoryItem.toJson() = JSONObject().apply {
    put("id", id); put("name", name); put("categoryId", categoryId); put("quantity", quantity); put("priceRon", priceRon)
    put("partNumber", partNumber); put("description", description); put("minStock", minStock); put("supplierId", supplierId)
}
private fun JSONObject.toInventoryItem() = InventoryItem(
    id = optLong("id"), name = getString("name"), categoryId = if (has("categoryId") && !isNull("categoryId")) optLong("categoryId") else null,
    quantity = optInt("quantity"), priceRon = optLong("priceRon"), partNumber = optString("partNumber").takeIf { it.isNotBlank() },
    description = optString("description").takeIf { it.isNotBlank() }, minStock = optInt("minStock"), supplierId = if (has("supplierId") && !isNull("supplierId")) optLong("supplierId") else null
)

private fun Supplier.toJson() = JSONObject().apply { put("id", id); put("name", name); put("contact", contact) }
private fun JSONObject.toSupplier() = Supplier(id = optLong("id"), name = getString("name"), contact = optString("contact").takeIf { it.isNotBlank() })

private fun Category.toJson() = JSONObject().apply { put("id", id); put("name", name) }
private fun JSONObject.toCategory() = Category(id = optLong("id"), name = getString("name"))

private fun FinancialTransaction.toJson() = JSONObject().apply {
    put("id", id); put("projectId", projectId); put("type", type.name); put("category", category); put("amountRon", amountRon); put("date", date)
}
private fun JSONObject.toFinancialTransaction() = FinancialTransaction(
    id = optLong("id"), projectId = if (has("projectId") && !isNull("projectId")) optLong("projectId") else null,
    type = TransactionType.valueOf(getString("type")), category = getString("category"), amountRon = optLong("amountRon"), date = getString("date")
)

private fun PaymentPlan.toJson() = JSONObject().apply { put("id", id); put("projectId", projectId); put("totalRon", totalRon) }
private fun JSONObject.toPaymentPlan() = PaymentPlan(id = optLong("id"), projectId = optLong("projectId"), totalRon = optLong("totalRon"))

private fun Installment.toJson() = JSONObject().apply { put("id", id); put("planId", planId); put("amountRon", amountRon); put("dueDate", dueDate); put("paid", paid) }
private fun JSONObject.toInstallment() = Installment(id = optLong("id"), planId = optLong("planId"), amountRon = optLong("amountRon"), dueDate = getString("dueDate"), paid = optBoolean("paid"))

private fun ProjectMaterialUsage.toJson() = JSONObject().apply { put("id", id); put("projectId", projectId); put("inventoryItemId", inventoryItemId); put("quantityUsed", quantityUsed); put("date", date) }
private fun JSONObject.toProjectMaterialUsage() = ProjectMaterialUsage(id = optLong("id"), projectId = optLong("projectId"), inventoryItemId = optLong("inventoryItemId"), quantityUsed = optInt("quantityUsed"), date = getString("date"))
