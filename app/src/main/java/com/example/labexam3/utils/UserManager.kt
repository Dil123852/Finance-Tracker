package com.example.labexam3.utils

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_NAME = "name"
        private const val KEY_AGE = "age"
    }

    fun saveUser(username: String, password: String, name: String, age: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putString(KEY_NAME, name)
            putString(KEY_AGE, age)
            apply()
        }
    }

    fun validateUser(username: String, password: String): Boolean {
        val savedUsername = sharedPreferences.getString(KEY_USERNAME, "")
        val savedPassword = sharedPreferences.getString(KEY_PASSWORD, "")
        return username == savedUsername && password == savedPassword
    }

    fun isUserRegistered(): Boolean {
        return !sharedPreferences.getString(KEY_USERNAME, "").isNullOrEmpty()
    }

    fun getUserName(): String {
        return sharedPreferences.getString(KEY_NAME, "") ?: ""
    }

    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }

    fun getUserAge(): String {
        return sharedPreferences.getString(KEY_AGE, "") ?: ""
    }
} 