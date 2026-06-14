package com.kasirku.app.data.local

import android.content.Context
import android.content.SharedPreferences

class StoreConfig(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("store_config", Context.MODE_PRIVATE)

    var storeName: String
        get() = prefs.getString("store_name", "Toko Sejahtera") ?: "Toko Sejahtera"
        set(value) = prefs.edit().putString("store_name", value).apply()

    var storeAddress: String
        get() = prefs.getString("store_address", "Jl. Merdeka No. 123, Jakarta") ?: "Jl. Merdeka No. 123, Jakarta"
        set(value) = prefs.edit().putString("store_address", value).apply()

    var storePhone: String
        get() = prefs.getString("store_phone", "021-555-0123") ?: "021-555-0123"
        set(value) = prefs.edit().putString("store_phone", value).apply()

    var taxPercent: Double
        get() = prefs.getFloat("tax_percent", 10f).toDouble()
        set(value) = prefs.edit().putFloat("tax_percent", value.toFloat()).apply()

    var receiptFooter: String
        get() = prefs.getString("receipt_footer", "Terima kasih!") ?: "Terima kasih!"
        set(value) = prefs.edit().putString("receipt_footer", value).apply()

    // Theme: "light", "dark", "auto"
    var themeMode: String
        get() = prefs.getString("theme_mode", "auto") ?: "auto"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    // Onboarding completed flag
    var onboardingCompleted: Boolean
        get() = prefs.getBoolean("onboarding_completed", false)
        set(value) = prefs.edit().putBoolean("onboarding_completed", value).apply()

    // Custom categories (comma-separated string)
    var customCategories: String
        get() = prefs.getString("custom_categories", "") ?: ""
        set(value) = prefs.edit().putString("custom_categories", value).apply()

    fun getCustomCategoriesList(): List<String> {
        val raw = customCategories
        return if (raw.isBlank()) emptyList()
        else raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun setCustomCategoriesList(list: List<String>) {
        customCategories = list.joinToString(",")
    }
}
