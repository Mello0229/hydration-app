package com.example.application.storage

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.application.models.TrainingSession
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "app_preferences")

private val ACTIVITY_LOGS_KEY = stringPreferencesKey("activity_logs")

suspend fun saveLogs(context: Context, logs: List<TrainingSession>) {
    val json = Gson().toJson(logs)
    context.dataStore.edit { prefs ->
        prefs[ACTIVITY_LOGS_KEY] = json
    }
}

suspend fun loadLogs(context: Context): List<TrainingSession> {
    val prefs = context.dataStore.data.first()
    val json = prefs[ACTIVITY_LOGS_KEY] ?: return emptyList()
    return Gson().fromJson(json, Array<TrainingSession>::class.java).toList()
}