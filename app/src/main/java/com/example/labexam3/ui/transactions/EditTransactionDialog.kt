package com.example.labexam3.ui.transactions

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.labexam3.R
import com.example.labexam3.databinding.DialogEditTransactionBinding
import com.example.labexam3.model.Transaction
import com.example.labexam3.model.TransactionCategory
import com.example.labexam3.model.TransactionType
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditTransactionDialog : DialogFragment() {
    private var _binding: DialogEditTransactionBinding? = null
    private val binding get() = _binding!!
    private var onSaveListener: ((Transaction) -> Unit)? = null
    private var transaction: Transaction? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditTransactionBinding.inflate(layoutInflater)
        
        // Setup category dropdown
        val categories = TransactionCategory.values().map { it.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)

        // Load transaction data
        transaction?.let { trans ->
            binding.etTitle.setText(trans.title)
            binding.etAmount.setText(trans.amount.toString())
            binding.actvCategory.setText(trans.category.toString(), false)
            binding.etNote.setText(trans.note)
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Transaction")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                saveTransaction()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    fun setTransaction(transaction: Transaction) {
        this.transaction = transaction
    }

    fun setOnSaveListener(listener: (Transaction) -> Unit) {
        this.onSaveListener = listener
    }

    private fun saveTransaction() {
        val title = binding.etTitle.text.toString()
        val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
        val category = TransactionCategory.valueOf(binding.actvCategory.text.toString())
        val note = binding.etNote.text.toString()

        transaction?.let { trans ->
            val updatedTransaction = trans.copy(
                title = title,
                amount = amount,
                category = category,
                note = note,
                type = trans.type,
                id = trans.id,
                date = trans.date
            )
            onSaveListener?.invoke(updatedTransaction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 