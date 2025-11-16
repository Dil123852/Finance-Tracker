package com.example.labexam3.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.R
import com.example.labexam3.databinding.ItemTransactionBinding
import com.example.labexam3.model.Transaction
import com.example.labexam3.model.TransactionType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit = { },
    private val onDeleteClick: (Transaction) -> Unit = { }
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        private var currentTransaction: Transaction? = null

        init {
            binding.root.setOnClickListener {
                currentTransaction?.let { transaction ->
                    showOptionsDialog(transaction)
                }
            }
        }

        fun bind(transaction: Transaction) {
            currentTransaction = transaction
            binding.titleTextView.text = transaction.title
            binding.amountTextView.text = formatAmount(transaction.amount)
            binding.categoryTextView.text = transaction.category.toString()
            binding.dateTextView.text = formatDate(transaction.date)
            binding.noteTextView.text = transaction.note

            // Set text color based on transaction type
            val color = if (transaction.type == TransactionType.INCOME) {
                android.graphics.Color.GREEN
            } else {
                android.graphics.Color.RED
            }
            binding.amountTextView.setTextColor(color)
        }

        private fun showOptionsDialog(transaction: Transaction) {
            MaterialAlertDialogBuilder(binding.root.context)
                .setTitle("Transaction Options")
                .setItems(arrayOf("Edit", "Delete")) { _, which ->
                    when (which) {
                        0 -> onTransactionClick(transaction)
                        1 -> onDeleteClick(transaction)
                    }
                }
                .show()
        }
    }

    private fun formatAmount(amount: Double): String {
        return String.format("Rs: %.2f", amount)
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} 