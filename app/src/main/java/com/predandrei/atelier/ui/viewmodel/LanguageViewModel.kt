package com.predandrei.atelier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predandrei.atelier.data.prefs.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {
    val languageTag: StateFlow<String?> = repo.languageTag.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun setLanguage(tag: String?) {
        viewModelScope.launch { repo.setLanguageTag(tag) }
    }
}
