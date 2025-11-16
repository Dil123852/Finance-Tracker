package com.example.labexam3.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.labexam3.R
import com.example.labexam3.databinding.DialogAddTransactionBinding
import com.example.labexam3.databinding.DialogSetBudgetBinding
import com.example.labexam3.databinding.FragmentHomeBinding
import com.example.labexam3.model.Transaction
import com.example.labexam3.model.TransactionCategory
import com.example.labexam3.model.TransactionType
import com.example.labexam3.ui.TransactionAdapter
import com.example.labexam3.utils.AccountManager
import com.example.labexam3.utils.PreferencesManager
import com.example.labexam3.viewmodel.TransactionViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.util.*
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
        viewModel.initialize(preferencesManager)
        adapter = TransactionAdapter()
        
        // Get user's name from AccountManager
        val accountManager = AccountManager(requireContext())
        val account = accountManager.getCurrentAccount()
        val greeting = if (account != null) {
            "Welcome, ${account.name}!"
        } else {
            "Welcome!"
        }
        binding.tvTitle.text = greeting
        
        setupBudgetCard()
        setupExpenseChart()
        setupRecentTransactions()
        setupAddTransactionButton()
        setupSetBudgetButton()
        setupObservers()
    }

    private fun setupBudgetCard() {
        val monthlyBudget = preferencesManager.monthlyBudget
        val currencyCode = preferencesManager.currency
        val transactions = preferencesManager.getTransactions()
        val currentMonthExpenses = calculateCurrentMonthExpenses(transactions)
        val remaining = monthlyBudget - currentMonthExpenses
        val progress = if (monthlyBudget > 0) (currentMonthExpenses / monthlyBudget * 100).toInt() else 0

        binding.budgetAmount.text = String.format("Rs: %.2f", monthlyBudget)
        binding.budgetProgress.progress = progress
        binding.budgetRemaining.text = "Remaining: Rs: %.2f".format(remaining)
    }

    private fun setupExpenseChart() {
        val transactions = preferencesManager.getTransactions()
        val entries = mutableListOf<PieEntry>()
        val categoryExpenses = calculateCategoryExpenses(transactions)

        if (categoryExpenses.isNotEmpty()) {
            categoryExpenses.forEach { (category, amount) ->
                entries.add(PieEntry(amount.toFloat(), category.toString()))
            }

            val dataSet = PieDataSet(entries, "Expenses by Category").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 14f
                valueTextColor = android.graphics.Color.WHITE
            }

            binding.expenseChart.apply {
                data = PieData(dataSet)
                description.isEnabled = false
                setUsePercentValues(true)
                setEntryLabelColor(android.graphics.Color.WHITE)
                setEntryLabelTextSize(12f)
                animateY(1000)
                invalidate()
            }
        } else {
            binding.expenseChart.clear()
            binding.expenseChart.invalidate()
        }
    }

    private fun setupRecentTransactions() {
        binding.recentTransactionsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
        }
    }

    private fun setupAddTransactionButton() {
        binding.fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }
    }

    private fun setupSetBudgetButton() {
        binding.btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactions.collect { transactions ->
                val recentTransactions = transactions.take(5)
                adapter.submitList(recentTransactions)
                updateUI(transactions)
            }
        }
    }

    private fun updateUI(transactions: List<Transaction>) {
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = totalIncome - totalExpense
        val monthlyBudget = preferencesManager.monthlyBudget

        binding.budgetAmount.text = String.format("Rs: %.2f", monthlyBudget)
        binding.budgetRemaining.text = "Remaining: Rs: %.2f".format(monthlyBudget - totalExpense + totalIncome)
        binding.budgetProgress.progress = if (monthlyBudget > 0) ((totalExpense / monthlyBudget) * 100).toInt() else 0

        // Update expense chart
        val entries = mutableListOf<PieEntry>()
        val categoryExpenses = calculateCategoryExpenses(transactions)

        if (categoryExpenses.isNotEmpty()) {
            categoryExpenses.forEach { (category, amount) ->
                entries.add(PieEntry(amount.toFloat(), category.toString()))
            }

            val dataSet = PieDataSet(entries, "Expenses by Category").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 14f
                valueTextColor = android.graphics.Color.WHITE
            }

            binding.expenseChart.apply {
                data = PieData(dataSet)
                description.isEnabled = false
                setUsePercentValues(true)
                setEntryLabelColor(android.graphics.Color.WHITE)
                setEntryLabelTextSize(12f)
                animateY(1000)
                invalidate()
            }
        } else {
            binding.expenseChart.clear()
            binding.expenseChart.invalidate()
        }
    }

    private fun showAddTransactionDialog() {
        val dialogBinding = DialogAddTransactionBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Setup transaction type dropdown
        val types = arrayOf("Income", "Expense")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        (dialogBinding.spinnerType as AutoCompleteTextView).setAdapter(typeAdapter)

        // Setup category dropdown
        val categories = TransactionCategory.values().map { it.toString() }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        (dialogBinding.spinnerCategory as AutoCompleteTextView).setAdapter(categoryAdapter)

        dialogBinding.buttonSave.setOnClickListener {
            val title = dialogBinding.editTitle.text.toString()
            val amount = dialogBinding.editAmount.text.toString().toDoubleOrNull() ?: 0.0
            val type = when (dialogBinding.spinnerType.text.toString()) {
                "Income" -> TransactionType.INCOME
                "Expense" -> TransactionType.EXPENSE
                else -> TransactionType.EXPENSE
            }
            val category = TransactionCategory.valueOf(dialogBinding.spinnerCategory.text.toString())
            val note = dialogBinding.editNote.text.toString()

            if (title.isNotEmpty() && amount > 0) {
            val transaction = Transaction(
                title = title,
                amount = amount,
                type = type,
                category = category,
                    note = note
            )
                viewModel.addTransaction(transaction)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showSetBudgetDialog() {
        val dialogBinding = DialogSetBudgetBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.etBudget.setText(preferencesManager.monthlyBudget.toString())

        dialogBinding.btnSave.setOnClickListener {
            val budget = dialogBinding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
            if (budget >= 0) {
            preferencesManager.monthlyBudget = budget
            setupBudgetCard()
            dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid budget amount", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun calculateCurrentMonthExpenses(transactions: List<Transaction>): Double {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return transactions.filter { transaction ->
            val transactionCalendar = Calendar.getInstance().apply {
                time = transaction.date
            }
            transaction.type == TransactionType.EXPENSE &&
            transactionCalendar.get(Calendar.MONTH) == currentMonth &&
                    transactionCalendar.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }
    }

    private fun calculateCategoryExpenses(transactions: List<Transaction>): Map<TransactionCategory, Double> {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 