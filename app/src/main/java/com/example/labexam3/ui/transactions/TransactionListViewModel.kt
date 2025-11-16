package com.example.labexam3.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.labexam3.model.Transaction
import com.example.labexam3.utils.PreferencesManager

class TransactionListViewModel : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private lateinit var preferencesManager: PreferencesManager

    fun initialize(preferencesManager: PreferencesManager) {
        this.preferencesManager = preferencesManager
        loadTransactions()
    }

    private fun loadTransactions() {
        _transactions.value = preferencesManager.getTransactions()
            .sortedByDescending { it.date }
    }

    fun refreshTransactions() {
        loadTransactions()
    }
} 