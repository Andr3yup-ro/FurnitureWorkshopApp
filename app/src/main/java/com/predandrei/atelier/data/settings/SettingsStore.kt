package com.predandrei.atelier.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val LANGUAGE = stringPreferencesKey("language") // "en" or "ro"
}

class SettingsStore(private val context: Context) {
    val language: Flow<String> = context.dataStore.data.map { prefs: Preferences ->
        prefs[SettingsKeys.LANGUAGE] ?: "system"
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[SettingsKeys.LANGUAGE] = lang }
    }
}
