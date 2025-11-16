package com.example.labexam3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labexam3.model.Transaction
import com.example.labexam3.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private lateinit var preferencesManager: PreferencesManager

    fun initialize(preferencesManager: PreferencesManager) {
        this.preferencesManager = preferencesManager
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _transactions.value = preferencesManager.getTransactions().sortedByDescending { it.date }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val currentTransactions = preferencesManager.getTransactions().toMutableList()
            currentTransactions.add(transaction)
            preferencesManager.saveTransactions(currentTransactions)
            _transactions.value = currentTransactions.sortedByDescending { it.date }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val currentTransactions = preferencesManager.getTransactions().toMutableList()
            val index = currentTransactions.indexOfFirst { it.id == transaction.id }
            if (index != -1) {
                currentTransactions[index] = transaction
                preferencesManager.saveTransactions(currentTransactions)
                _transactions.value = currentTransactions.sortedByDescending { it.date }
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val currentTransactions = preferencesManager.getTransactions().toMutableList()
            val index = currentTransactions.indexOfFirst { it.id == transaction.id }
            if (index != -1) {
                currentTransactions.removeAt(index)
                preferencesManager.saveTransactions(currentTransactions)
                _transactions.value = currentTransactions.sortedByDescending { it.date }
            }
        }
    }
} 