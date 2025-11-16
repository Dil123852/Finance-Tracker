package com.example.labexam3.model

import java.util.Date
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: TransactionCategory = TransactionCategory.OTHER,
    val date: Date = Date(),
    val note: String = ""
) 