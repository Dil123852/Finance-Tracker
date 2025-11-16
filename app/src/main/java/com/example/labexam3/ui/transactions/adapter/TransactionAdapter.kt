package com.example.labexam3.ui.transactions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.databinding.ItemTransactionBinding
import com.example.labexam3.model.Transaction
import com.example.labexam3.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit = { }
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTransactionClick(getItem(position))
                }
            }
        }

        fun bind(transaction: Transaction) {
            binding.titleTextView.text = transaction.title
            binding.amountTextView.text = String.format("Rs: %.2f", transaction.amount)
            binding.categoryTextView.text = transaction.category.toString()
            binding.dateTextView.text = formatDate(transaction.date)
            binding.noteTextView.text = transaction.note

            // Set text color based on transaction type
            val color = when (transaction.type) {
                TransactionType.INCOME -> android.graphics.Color.GREEN
                TransactionType.EXPENSE -> android.graphics.Color.RED
            }
            binding.amountTextView.setTextColor(color)
        }

        private fun formatDate(date: Date): String {
            return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
        }
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