package com.example.meetingrecorder

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class ApiKeyManager(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "api_keys",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAssemblyAiApiKey(apiKey: String) {
        sharedPreferences.edit().putString("assemblyai_api_key", apiKey).apply()
    }

    fun getAssemblyAiApiKey(): String? {
        return sharedPreferences.getString("assemblyai_api_key", null)
    }

    fun saveMistralApiKey(apiKey: String) {
        sharedPreferences.edit().putString("mistral_api_key", apiKey).apply()
    }

    fun getMistralApiKey(): String? {
        return sharedPreferences.getString("mistral_api_key", null)
    }
}
