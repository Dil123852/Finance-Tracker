package com.example.labexam3.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.labexam3.model.Transaction

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private var currentUsername: String = ""

    companion object {
        private const val PREFS_NAME = "LabExam3Prefs"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget_%s"
        private const val KEY_CURRENCY = "currency_%s"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled_%s"
        private const val KEY_TRANSACTIONS = "transactions_%s"
        private const val TAG = "PreferencesManager"
    }

    fun setCurrentUser(username: String) {
        Log.d(TAG, "Setting current user to: $username")
        currentUsername = username
        sharedPreferences.edit().putString(KEY_CURRENT_USER, username).apply()
    }

    fun getCurrentUser(): String {
        if (currentUsername.isEmpty()) {
            currentUsername = sharedPreferences.getString(KEY_CURRENT_USER, "") ?: ""
            Log.d(TAG, "Retrieved current user: $currentUsername")
        }
        return currentUsername
    }

    private fun getKey(key: String): String {
        val user = getCurrentUser()
        val formattedKey = String.format(key, user)
        Log.d(TAG, "Generated key: $formattedKey for user: $user")
        return formattedKey
    }

    var monthlyBudget: Double
        get() {
            val value = sharedPreferences.getFloat(getKey(KEY_MONTHLY_BUDGET), 0f).toDouble()
            Log.d(TAG, "Getting monthly budget for ${getCurrentUser()}: $value")
            return value
        }
        set(value) {
            Log.d(TAG, "Setting monthly budget for ${getCurrentUser()}: $value")
            sharedPreferences.edit().putFloat(getKey(KEY_MONTHLY_BUDGET), value.toFloat()).apply()
        }

    var currency: String
        get() {
            val value = sharedPreferences.getString(getKey(KEY_CURRENCY), "USD") ?: "USD"
            Log.d(TAG, "Getting currency for ${getCurrentUser()}: $value")
            return value
        }
        set(value) {
            Log.d(TAG, "Setting currency for ${getCurrentUser()}: $value")
            sharedPreferences.edit().putString(getKey(KEY_CURRENCY), value).apply()
        }

    var notificationsEnabled: Boolean
        get() {
            val value = sharedPreferences.getBoolean(getKey(KEY_NOTIFICATIONS_ENABLED), true)
            Log.d(TAG, "Getting notifications enabled for ${getCurrentUser()}: $value")
            return value
        }
        set(value) {
            Log.d(TAG, "Setting notifications enabled for ${getCurrentUser()}: $value")
            sharedPreferences.edit().putBoolean(getKey(KEY_NOTIFICATIONS_ENABLED), value).apply()
        }

    fun getTransactions(): List<Transaction> {
        val key = getKey(KEY_TRANSACTIONS)
        val json = sharedPreferences.getString(key, null)
        val transactions = if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson<List<Transaction>>(json, type)
        } else {
            emptyList()
        }
        Log.d(TAG, "Getting transactions for ${getCurrentUser()}: ${transactions.size} transactions")
        return transactions
    }

    fun addTransaction(transaction: Transaction) {
        val currentTransactions = getTransactions().toMutableList()
        currentTransactions.add(transaction)
        saveTransactions(currentTransactions)
        Log.d(TAG, "Added transaction for ${getCurrentUser()}: ${transaction.title} (${transaction.amount} ${transaction.type})")
    }

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        val key = getKey(KEY_TRANSACTIONS)
        sharedPreferences.edit().putString(key, json).apply()
        Log.d(TAG, "Saved ${transactions.size} transactions for ${getCurrentUser()}")
    }

    fun clearAll() {
        val editor = sharedPreferences.edit()
        val user = getCurrentUser()
        editor.remove(getKey(KEY_MONTHLY_BUDGET))
        editor.remove(getKey(KEY_CURRENCY))
        editor.remove(getKey(KEY_NOTIFICATIONS_ENABLED))
        editor.remove(getKey(KEY_TRANSACTIONS))
        editor.apply()
        Log.d(TAG, "Cleared all data for user: $user")
    }

    fun clearUserData(username: String) {
        val editor = sharedPreferences.edit()
        editor.remove(String.format(KEY_MONTHLY_BUDGET, username))
        editor.remove(String.format(KEY_CURRENCY, username))
        editor.remove(String.format(KEY_NOTIFICATIONS_ENABLED, username))
        editor.remove(String.format(KEY_TRANSACTIONS, username))
        editor.apply()
        Log.d(TAG, "Cleared all data for user: $username")
    }
} 