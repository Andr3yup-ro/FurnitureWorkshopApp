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
