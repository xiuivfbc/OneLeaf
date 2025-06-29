package com.example.todolist.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "database_manager")

class PreferencesDataStore(private val context: Context) {
    private companion object {
        val DATABASE_NAMES_KEY = stringSetPreferencesKey("database_names")
    }

    suspend fun saveDatabaseNames(names: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[DATABASE_NAMES_KEY] = names
        }
    }

    val databaseNames: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[DATABASE_NAMES_KEY]?.toList() ?: emptyList()
        }
}
