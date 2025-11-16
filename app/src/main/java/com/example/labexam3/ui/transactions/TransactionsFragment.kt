package com.example.labexam3.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.labexam3.databinding.FragmentTransactionsBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupAddTransactionButton()
    }

    private fun setupViewPager() {
        val pagerAdapter = TransactionsPagerAdapter(requireActivity())
        binding.viewPager2.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "All"
                1 -> "Income"
                2 -> "Expenses"
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }.attach()
    }

    private fun setupAddTransactionButton() {
        binding.fabAddTransaction.setOnClickListener {
            // TODO: Navigate to add transaction screen
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 