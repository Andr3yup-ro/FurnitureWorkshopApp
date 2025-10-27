package com.predandrei.atelier.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate
import com.predandrei.atelier.ui.viewmodel.LanguageViewModel

@Composable
fun AppLanguageApplier(vm: LanguageViewModel = hiltViewModel()) {
    val tag by vm.languageTag.collectAsState()
    LaunchedEffect(tag) {
        if (tag.isNullOrBlank()) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
    }
}
