package com.example.labexam3.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.labexam3.R
import com.example.labexam3.databinding.FragmentTransactionListBinding
import com.example.labexam3.model.Transaction
import com.example.labexam3.model.TransactionType
import com.example.labexam3.ui.TransactionAdapter
import com.example.labexam3.utils.PreferencesManager
import com.example.labexam3.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var adapter: TransactionAdapter
    private var transactionType: TransactionType? = null

    companion object {
        private const val ARG_TRANSACTION_TYPE = "transaction_type"

        fun newInstance(type: TransactionType): TransactionListFragment {
            return TransactionListFragment().apply {
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
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesManager = PreferencesManager(requireContext())
        viewModel.initialize(preferencesManager)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            // Handle transaction click
        }
        binding.viewPager.adapter = object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> TransactionListContentFragment.newInstance(TransactionType.INCOME)
                    1 -> TransactionListContentFragment.newInstance(TransactionType.EXPENSE)
                    else -> throw IllegalArgumentException("Invalid position: $position")
                }
            }
        }

        // Set up tab titles
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Incomes"
                1 -> "Expenses"
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }.attach()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactions.collect { transactions ->
                val filteredTransactions = transactions.filter { it.type == transactionType }
                adapter.submitList(filteredTransactions)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 