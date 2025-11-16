package com.example.labexam3.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.labexam3.databinding.FragmentTransactionListContentBinding
import com.example.labexam3.model.TransactionType
import com.example.labexam3.ui.TransactionAdapter
import com.example.labexam3.utils.PreferencesManager
import com.example.labexam3.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class TransactionListContentFragment : Fragment() {

    private var _binding: FragmentTransactionListContentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var adapter: TransactionAdapter
    private var transactionType: TransactionType? = null

    companion object {
        private const val ARG_TRANSACTION_TYPE = "transaction_type"

        fun newInstance(type: TransactionType): TransactionListContentFragment {
            return TransactionListContentFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TRANSACTION_TYPE, type)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionType = arguments?.getSerializable(ARG_TRANSACTION_TYPE) as? TransactionType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionListContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
        viewModel.initialize(preferencesManager)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onTransactionClick = { transaction ->
                val dialog = EditTransactionDialog().apply {
                    setTransaction(transaction)
                    setOnSaveListener { updatedTransaction ->
                        viewModel.updateTransaction(updatedTransaction)
                    }
                }
                dialog.show(childFragmentManager, "EditTransactionDialog")
            },
            onDeleteClick = { transaction ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteTransaction(transaction)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TransactionListContentFragment.adapter
        }

        // Update summary title based on transaction type
        binding.summaryTitle.text = when (transactionType) {
            TransactionType.INCOME -> "Income Summary"
            TransactionType.EXPENSE -> "Expense Summary"
            else -> "Summary"
        }

        // Initial load of transactions
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactions.collect { allTransactions ->
                val filteredTransactions = allTransactions
                    .filter { it.type == transactionType }
                    .sortedByDescending { it.date }
                    .toList()
                
                adapter.submitList(filteredTransactions)
                
                // Update summary
                val totalAmount = filteredTransactions.sumOf { it.amount }
                val formatter = java.text.DecimalFormat("'Rs.' #,##0.00")
                
                binding.totalAmount.text = formatter.format(totalAmount)
                binding.totalAmount.setTextColor(
                    if (transactionType == TransactionType.INCOME) {
                        android.graphics.Color.GREEN
                    } else {
                        android.graphics.Color.RED
                    }
                )
                binding.transactionCount.text = filteredTransactions.size.toString()

                // Update empty state visibility
                binding.emptyText.visibility = if (filteredTransactions.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                binding.recyclerView.visibility = if (filteredTransactions.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 