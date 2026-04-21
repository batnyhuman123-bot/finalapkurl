package com.example.finalapkurl.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore(name = "scan_history")

class HistoryRepository(
    private val context: Context
) {
    private val gson = Gson()
    private val key = stringPreferencesKey("entries_json")

    private fun loadList(prefs: Preferences): List<ScanHistoryRecord> {
        val json = prefs[key] ?: return emptyList()
        val type = object : TypeToken<List<ScanHistoryRecord>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun observeAll(): Flow<List<ScanHistoryRecord>> =
        context.historyDataStore.data.map { prefs ->
            loadList(prefs).sortedByDescending { it.createdAtMs }
        }

    fun observeLatest5(): Flow<List<ScanHistoryRecord>> =
        observeAll().map { it.take(5) }

    fun observeTotalCount(): Flow<Int> =
        observeAll().map { it.size }

    fun observeHighRiskCount(): Flow<Int> =
        observeAll().map { list -> list.count { it.isHighRisk } }

    suspend fun insert(record: ScanHistoryRecord): Long {
        var newId = 0L
        context.historyDataStore.edit { prefs ->
            val list = loadList(prefs).toMutableList()
            newId = (list.maxOfOrNull { it.id } ?: 0L) + 1L
            list.add(0, record.copy(id = newId))
            prefs[key] = gson.toJson(list)
        }
        return newId
    }

    suspend fun getById(id: Long): ScanHistoryRecord? {
        val prefs = context.historyDataStore.data.first()
        return loadList(prefs).find { it.id == id }
    }

    suspend fun clearAll() {
        context.historyDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }

    suspend fun deleteByIds(ids: Set<Long>) {
        if (ids.isEmpty()) return
        context.historyDataStore.edit { prefs ->
            val list = loadList(prefs).filterNot { it.id in ids }
            prefs[key] = gson.toJson(list)
        }
    }
}
