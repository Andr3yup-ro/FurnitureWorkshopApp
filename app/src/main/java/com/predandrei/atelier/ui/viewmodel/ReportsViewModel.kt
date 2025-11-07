package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.util.reporting.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val exporter: PdfExporter
) : ViewModel() {
    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    fun exportAll(uri: Uri, resolver: ContentResolver, onDone: () -> Unit = {}) {
        _status.value = "Generating PDF…"
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                resolver.openOutputStream(uri)?.use { out ->
                    exporter.exportAllToPdf(out)
                } ?: error("Cannot open output stream")
            }
            result
                .onSuccess { _status.value = "Report generated" }
                .onFailure { _status.value = "Failed: ${it.message}" }
            onDone()
        }
    }

    fun exportProjects(uri: Uri, resolver: ContentResolver, onDone: () -> Unit = {}) {
        _status.value = "Generating Projects PDF…"
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                resolver.openOutputStream(uri)?.use { out ->
                    exporter.exportProjectsToPdf(out)
                } ?: error("Cannot open output stream")
            }
            result
                .onSuccess { _status.value = "Report generated" }
                .onFailure { _status.value = "Failed: ${it.message}" }
            onDone()
        }
    }

    fun exportInventory(uri: Uri, resolver: ContentResolver, onDone: () -> Unit = {}) {
        _status.value = "Generating Inventory PDF…"
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                resolver.openOutputStream(uri)?.use { out ->
                    exporter.exportInventoryToPdf(out)
                } ?: error("Cannot open output stream")
            }
            result
                .onSuccess { _status.value = "Report generated" }
                .onFailure { _status.value = "Failed: ${it.message}" }
            onDone()
        }
    }

    fun exportFinance(uri: Uri, resolver: ContentResolver, onDone: () -> Unit = {}) {
        _status.value = "Generating Finance PDF…"
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                resolver.openOutputStream(uri)?.use { out ->
                    exporter.exportFinanceToPdf(out)
                } ?: error("Cannot open output stream")
            }
            result
                .onSuccess { _status.value = "Report generated" }
                .onFailure { _status.value = "Failed: ${it.message}" }
            onDone()
        }
    }

    fun exportProject(uri: Uri, resolver: ContentResolver, projectId: Long, onDone: () -> Unit = {}) {
        _status.value = "Generating Project PDF…"
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                resolver.openOutputStream(uri)?.use { out ->
                    exporter.exportProjectToPdf(out, projectId)
                } ?: error("Cannot open output stream")
            }
            result
                .onSuccess { _status.value = "Report generated" }
                .onFailure { _status.value = "Failed: ${it.message}" }
            onDone()
        }
    }
}
