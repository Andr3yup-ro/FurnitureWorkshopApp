package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.util.reporting.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun exportAll(output: OutputStream, onDone: () -> Unit = {}) {
        _status.value = "Generating PDF…"
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { exporter.exportAllToPdf(output) }
                .onSuccess { _status.value = "Report generated" }
                .onFailure { _status.value = "Failed: ${it.message}" }
            onDone()
        }
    }
}
