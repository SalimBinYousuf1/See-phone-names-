package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "salim_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val USE_DEVICE_CONTACTS = booleanPreferencesKey("use_device_contacts")
        val ASK_BEFORE_ONLINE = booleanPreferencesKey("ask_before_online")
        val ENABLE_AUTHORIZED_PROVIDER = booleanPreferencesKey("enable_authorized_provider")
        val SAVE_LOOKUP_HISTORY = booleanPreferencesKey("save_lookup_history")
        val RETENTION_DAYS = intPreferencesKey("history_retention_days")
        val DEFAULT_COUNTRY = stringPreferencesKey("default_country")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    }

    val useDeviceContacts: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.USE_DEVICE_CONTACTS] ?: true
    }

    val askBeforeOnlineLookup: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.ASK_BEFORE_ONLINE] ?: false
    }

    val enableAuthorizedProvider: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.ENABLE_AUTHORIZED_PROVIDER] ?: true
    }

    val saveLookupHistory: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.SAVE_LOOKUP_HISTORY] ?: true
    }

    val historyRetentionDays: Flow<Int> = context.dataStore.data.map {
        it[Keys.RETENTION_DAYS] ?: 30
    }

    val defaultCountry: Flow<String> = context.dataStore.data.map {
        it[Keys.DEFAULT_COUNTRY] ?: "US"
    }

    val hapticFeedbackEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.HAPTIC_FEEDBACK] ?: true
    }

    suspend fun setUseDeviceContacts(enabled: Boolean) {
        context.dataStore.edit { it[Keys.USE_DEVICE_CONTACTS] = enabled }
    }

    suspend fun setAskBeforeOnlineLookup(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ASK_BEFORE_ONLINE] = enabled }
    }

    suspend fun setEnableAuthorizedProvider(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_AUTHORIZED_PROVIDER] = enabled }
    }

    suspend fun setSaveLookupHistory(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SAVE_LOOKUP_HISTORY] = enabled }
    }

    suspend fun setHistoryRetentionDays(days: Int) {
        context.dataStore.edit { it[Keys.RETENTION_DAYS] = days }
    }

    suspend fun setDefaultCountry(countryCode: String) {
        context.dataStore.edit { it[Keys.DEFAULT_COUNTRY] = countryCode }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun clearSettings() {
        context.dataStore.edit { it.clear() }
    }
}
