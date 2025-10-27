package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.backup.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repo: BackupRepository
) : ViewModel() {
    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    fun backup() {
        viewModelScope.launch {
            val f: File = repo.backup()
            _status.value = "Backup saved: ${f.absolutePath}"
        }
    }

    fun restore() {
        viewModelScope.launch {
            val f = repo.restore()
            _status.value = if (f != null) "Restored from: ${f.absolutePath}" else "No backup file found"
        }
    }
}
