package com.example.memoryexplorer.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class LoginRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember")
    }

    val email = dataStore.data.map { it[EMAIL_KEY] ?: "" }
    val password = dataStore.data.map { it[PASSWORD_KEY] ?: "" }
    val remember = dataStore.data.map { it[REMEMBER_ME_KEY] ?: false }

    suspend fun setEmail(value: String) = dataStore.edit { it[EMAIL_KEY] = value }
    suspend fun setPassword(value: String) = dataStore.edit { it[PASSWORD_KEY] = value }
    suspend fun setRemember(value: Boolean) = dataStore.edit { it[REMEMBER_ME_KEY] = value }

    suspend fun logout() {
        setEmail("")
        setPassword("")
        setRemember(false)
    }

}