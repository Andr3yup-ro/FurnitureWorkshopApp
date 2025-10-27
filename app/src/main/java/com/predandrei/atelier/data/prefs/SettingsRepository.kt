package com.predandrei.atelier.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private object Keys {
        val LANGUAGE = stringPreferencesKey("language_tag")
    }

    val languageTag: Flow<String?> = context.settingsDataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE]
    }

    suspend fun setLanguageTag(tag: String?) {
        context.settingsDataStore.edit { prefs: Preferences ->
            if (tag == null) prefs.remove(Keys.LANGUAGE) else prefs[Keys.LANGUAGE] = tag
        }
    }
}
