package com.predandrei.atelier.util.reporting

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.predandrei.atelier.data.db.AppDatabase
import com.predandrei.atelier.util.CurrencyRon
import kotlinx.coroutines.flow.first
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfExporter @Inject constructor(
    private val db: AppDatabase
) {
    suspend fun exportProjectToPdf(output: OutputStream, projectId: Long) {
        val project = db.projectDao().getById(projectId)
        val client = project?.clientId?.let { id -> db.clientDao().getById(id) }
        val plan = db.paymentDao().getPlanByProjectId(projectId)
        val installments = plan?.let { db.paymentDao().getInstallments(it.id).first() } ?: emptyList()
        val materials = db.projectMaterialsDao().getByProject(projectId).first()
        val labor = db.laborDao().getByProject(projectId).first()

        val doc = PdfDocument()
        var page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 12f }
        var y = 24f
        fun title(t: String) { paint.textSize = 16f; canvas.drawText(t, 24f, y, paint); y += 20f; paint.textSize = 12f }
        fun line(t: String) { canvas.drawText(t, 24f, y, paint); y += 16f }
        fun newPage() { doc.finishPage(page); val n = doc.pages.size + 1; page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, n).create()); y = 24f }

        title("Project Report")
        line("Generated: ${java.time.LocalDateTime.now()}")
        y += 8f
        if (project != null) {
            title("#${project.id} ${project.title}")
            line("Status: ${project.status}")
            client?.let { line("Client: ${it.name} ${it.phone ?: ""}") }
            line("Value: ${CurrencyRon.formatMinorUnits(project.valueRon)}")
        } else {
            line("Project not found")
        }

        y += 8f
        title("Payment Plan")
        if (plan != null) {
            line("Total: ${CurrencyRon.formatMinorUnits(plan.totalRon)}  Advance: ${CurrencyRon.formatMinorUnits(plan.advanceRon)}  Remaining: ${CurrencyRon.formatMinorUnits((plan.totalRon - plan.advanceRon).coerceAtLeast(0))}")
            y += 6f
            installments.forEach { i -> if (y > 800f) newPage(); line("${i.dueDate} — ${CurrencyRon.formatMinorUnits(i.amountRon)} — ${if (i.paid) "PAID" else "UNPAID"}") }
        } else {
            line("No plan")
        }

        y += 8f
        title("Materials (${materials.size})")
        materials.take(80).forEach { m -> if (y > 800f) newPage(); line("Item ${m.inventoryItemId} — qty ${m.quantityUsed} on ${m.date}") }
        if (materials.size > 80) { if (y > 800f) newPage(); line("…and ${materials.size - 80} more") }

        y += 8f
        title("Labor (${labor.size})")
        labor.take(80).forEach { l -> if (y > 800f) newPage(); line("${l.minutes} min @ ${CurrencyRon.formatMinorUnits(l.hourlyRateRon)}/h on ${l.date}") }

        doc.finishPage(page)
        doc.writeTo(output)
        doc.close()
        output.flush()
    }
    suspend fun exportProjectsToPdf(output: OutputStream) {
        val projects = db.projectDao().getAll().first()
        val clients = db.clientDao().getAll().first()
        val doc = PdfDocument()
        var page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 12f }
        var y = 24f
        fun title(t: String) { paint.textSize = 16f; canvas.drawText(t, 24f, y, paint); y += 20f; paint.textSize = 12f }
        fun line(t: String) { canvas.drawText(t, 24f, y, paint); y += 16f }
        fun newPage() { doc.finishPage(page); val n = doc.pages.size + 1; page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, n).create()); y = 24f }
        title("Projects Report")
        line("Generated: ${java.time.LocalDateTime.now()}")
        y += 8f
        projects.forEach { p ->
            if (y > 800f) newPage()
            val clientName = clients.firstOrNull { it.id == p.clientId }?.name ?: "-"
            line("#${p.id} ${p.title} — ${p.status} — ${clientName} — ${CurrencyRon.formatMinorUnits(p.valueRon)}")
        }
        doc.finishPage(page)
        doc.writeTo(output)
        doc.close()
        output.flush()
    }

    suspend fun exportInventoryToPdf(output: OutputStream) {
        val items = db.inventoryDao().getAll().first()
        val doc = PdfDocument()
        var page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 12f }
        var y = 24f
        fun title(t: String) { paint.textSize = 16f; canvas.drawText(t, 24f, y, paint); y += 20f; paint.textSize = 12f }
        fun line(t: String) { canvas.drawText(t, 24f, y, paint); y += 16f }
        fun newPage() { doc.finishPage(page); val n = doc.pages.size + 1; page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, n).create()); y = 24f }
        title("Inventory Report")
        line("Generated: ${java.time.LocalDateTime.now()}")
        y += 8f
        items.forEach { it ->
            if (y > 800f) newPage()
            line("#${it.id} ${it.name} — Qty: ${it.quantity} — Min: ${it.minStock} — ${CurrencyRon.formatMinorUnits(it.priceRon)}")
        }
        doc.finishPage(page)
        doc.writeTo(output)
        doc.close()
        output.flush()
    }

    suspend fun exportFinanceToPdf(output: OutputStream) {
        val txs = db.financeDao().getAll().first()
        val doc = PdfDocument()
        var page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 12f }
        var y = 24f
        fun title(t: String) { paint.textSize = 16f; canvas.drawText(t, 24f, y, paint); y += 20f; paint.textSize = 12f }
        fun line(t: String) { canvas.drawText(t, 24f, y, paint); y += 16f }
        fun newPage() { doc.finishPage(page); val n = doc.pages.size + 1; page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, n).create()); y = 24f }
        title("Finance Report")
        line("Generated: ${java.time.LocalDateTime.now()}")
        y += 8f
        val rev = txs.filter { it.type.name == "REVENUE" }.sumOf { it.amountRon }
        val exp = txs.filter { it.type.name == "EXPENSE" }.sumOf { it.amountRon }
        line("Revenue: ${CurrencyRon.formatMinorUnits(rev)}  Expenses: ${CurrencyRon.formatMinorUnits(exp)}  Profit: ${CurrencyRon.formatMinorUnits(rev - exp)}")
        y += 8f
        txs.forEach { t ->
            if (y > 800f) newPage()
            line("${t.date} — ${t.type} — ${t.category} — ${CurrencyRon.formatMinorUnits(t.amountRon)}")
        }
        doc.finishPage(page)
        doc.writeTo(output)
        doc.close()
        output.flush()
    }

    suspend fun exportAllToPdf(output: OutputStream) {
        val clients = db.clientDao().getAll().first()
        val projects = db.projectDao().getAll().first()
        val inventory = db.inventoryDao().getAll().first()
        val txs = db.financeDao().getAll().first()
        val installments = db.paymentDao().getAllInstallments().first()
        val materials = db.projectMaterialsDao().getAll().first()
        val labor = db.laborDao().getAll().first()

        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 portrait
        var page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 12f }

        var y = 24f
        fun title(text: String) { paint.textSize = 16f; canvas.drawText(text, 24f, y, paint); y += 20f; paint.textSize = 12f }
        fun line(text: String) { canvas.drawText(text, 24f, y, paint); y += 16f }
        fun newPage() {
            doc.finishPage(page)
            val n = doc.pages.size + 1
            page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, n).create())
            y = 24f
        }

        title("Manager Prestan — Full Report")
        line("Generated: ${java.time.LocalDateTime.now()}")
        y += 8f

        // Projects
        title("Projects (${projects.size})")
        projects.forEach { p ->
            if (y > 800f) newPage()
            line("#${p.id} ${p.title} — ${p.status} — Value: ${CurrencyRon.formatMinorUnits(p.valueRon)}")
        }

        y += 8f
        title("Clients (${clients.size})")
        clients.forEach { c -> if (y > 800f) newPage(); line("#${c.id} ${c.name} ${c.phone ?: ""}") }

        y += 8f
        title("Inventory (${inventory.size})")
        inventory.take(80).forEach { it -> if (y > 800f) newPage(); line("#${it.id} ${it.name} — Qty: ${it.quantity} — Min: ${it.minStock}") }
        if (inventory.size > 80) { if (y > 800f) newPage(); line("…and ${inventory.size - 80} more") }

        y += 8f
        title("Finance (${txs.size})")
        val rev = txs.filter { it.type.name == "REVENUE" }.sumOf { it.amountRon }
        val exp = txs.filter { it.type.name == "EXPENSE" }.sumOf { it.amountRon }
        line("Revenue: ${CurrencyRon.formatMinorUnits(rev)}  Expenses: ${CurrencyRon.formatMinorUnits(exp)}  Profit: ${CurrencyRon.formatMinorUnits(rev - exp)}")

        y += 8f
        title("Installments (${installments.size})")
        installments.take(80).forEach { i -> if (y > 800f) newPage(); line("Plan ${i.planId} — ${i.dueDate} — ${CurrencyRon.formatMinorUnits(i.amountRon)} — ${if (i.paid) "PAID" else "UNPAID"}") }

        y += 8f
        title("Materials Usage (${materials.size})")
        materials.take(80).forEach { m -> if (y > 800f) newPage(); line("Proj ${m.projectId} item ${m.inventoryItemId} — qty ${m.quantityUsed} on ${m.date}") }

        y += 8f
        title("Labor (${labor.size})")
        labor.take(80).forEach { l -> if (y > 800f) newPage(); line("Proj ${l.projectId} — ${l.minutes} min @ ${CurrencyRon.formatMinorUnits(l.hourlyRateRon)}/h on ${l.date}") }

        doc.finishPage(page)
        doc.writeTo(output)
        doc.close()
        output.flush()
    }
}
