package com.example.labexam3.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AccountManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("ACCOUNTS_PREFS", Context.MODE_PRIVATE)
    private val gson = Gson()
    private var currentAccount: Account? = null

    data class Account(
        val username: String,
        val password: String,
        val email: String,
        val phone: String,
        val name: String,
        val age: String
    )

    fun saveAccount(account: Account) {
        val accounts = getAllAccounts().toMutableList()
        accounts.add(account)
        val accountsJson = gson.toJson(accounts)
        sharedPreferences.edit().putString("accounts", accountsJson).apply()
    }

    fun getAllAccounts(): List<Account> {
        val accountsJson = sharedPreferences.getString("accounts", "[]")
        val type = object : TypeToken<List<Account>>() {}.type
        return gson.fromJson(accountsJson, type) ?: emptyList()
    }

    fun getAccount(username: String): Account? {
        return getAllAccounts().find { it.username == username }
    }

    fun deleteAccount(username: String) {
        val accounts = getAllAccounts().filter { it.username != username }
        val accountsJson = gson.toJson(accounts)
        sharedPreferences.edit().putString("accounts", accountsJson).apply()
        
        // Delete account-specific data
        context.getSharedPreferences("ACCOUNT_${username}", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun updateAccount(account: Account) {
        val accounts = getAllAccounts().toMutableList()
        val index = accounts.indexOfFirst { it.username == account.username }
        if (index != -1) {
            accounts[index] = account
            val accountsJson = gson.toJson(accounts)
            sharedPreferences.edit().putString("accounts", accountsJson).apply()
        }
    }

    fun setCurrentAccount(account: Account) {
        currentAccount = account
        sharedPreferences.edit().putString("current_account", account.username).apply()
    }

    fun getCurrentAccount(): Account? {
        if (currentAccount == null) {
            val currentUsername = sharedPreferences.getString("current_account", null)
            currentAccount = currentUsername?.let { getAccount(it) }
        }
        return currentAccount
    }

    fun clearCurrentAccount() {
        currentAccount = null
        sharedPreferences.edit().remove("current_account").apply()
    }

    fun getAccountPreferences(username: String): SharedPreferences {
        return context.getSharedPreferences("ACCOUNT_${username}", Context.MODE_PRIVATE)
    }

    fun saveAccountData(username: String, key: String, value: String) {
        getAccountPreferences(username).edit().putString(key, value).apply()
    }

    fun getAccountData(username: String, key: String, defaultValue: String = ""): String {
        return getAccountPreferences(username).getString(key, defaultValue) ?: defaultValue
    }
} 