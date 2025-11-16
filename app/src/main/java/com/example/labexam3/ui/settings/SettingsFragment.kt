package com.example.labexam3.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labexam3.R
import com.example.labexam3.databinding.FragmentSettingsBinding
import com.example.labexam3.utils.AccountManager
import com.example.labexam3.utils.PreferencesManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var accountManager: AccountManager
    private lateinit var preferencesManager: PreferencesManager
    private val gson = Gson()

    private val backupLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val outputStream = requireContext().contentResolver.openOutputStream(uri)
                    val writer = BufferedWriter(OutputStreamWriter(outputStream))
                    val backupData = BackupData(
                        transactions = preferencesManager.getTransactions(),
                        monthlyBudget = preferencesManager.monthlyBudget,
                        currency = preferencesManager.currency,
                        notificationsEnabled = preferencesManager.notificationsEnabled
                    )
                    writer.write(gson.toJson(backupData))
                    writer.close()
                    Toast.makeText(requireContext(), "Backup created successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to create backup: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val restoreLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val json = reader.readText()
                    val backupData = gson.fromJson(json, BackupData::class.java)
                    
                    preferencesManager.saveTransactions(backupData.transactions)
                    preferencesManager.monthlyBudget = backupData.monthlyBudget
                    preferencesManager.currency = backupData.currency
                    preferencesManager.notificationsEnabled = backupData.notificationsEnabled
                    
                    reader.close()
                    Toast.makeText(requireContext(), "Data restored successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to restore data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        accountManager = AccountManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()
        setupLanguageDropdown()
        setupCurrencyDropdown()
        setupLogoutButton()
        setupDataManagementButtons()
    }

    private fun setupDataManagementButtons() {
        binding.btnBackup.setOnClickListener {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, "finance_backup_$timestamp.json")
            }
            backupLauncher.launch(intent)
        }

        binding.btnRestore.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
            }
            restoreLauncher.launch(intent)
        }

        binding.btnReset.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reset All Data")
                .setMessage("Are you sure you want to delete all transactions? This action cannot be undone.")
                .setPositiveButton("Reset") { _, _ ->
                    preferencesManager.saveTransactions(emptyList())
                    preferencesManager.monthlyBudget = 0.0
                    Toast.makeText(requireContext(), "All data has been reset", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupUserProfile() {
        val account = accountManager.getCurrentAccount()
        if (account != null) {
            binding.tvFullName.text = "Full Name: ${account.name}"
            binding.tvUsername.text = "Username: ${account.username}"
            binding.tvAge.text = "Age: ${account.age}"
        }
    }

    private fun setupLanguageDropdown() {
        val languages = arrayOf("English", "Spanish", "French", "German")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, languages)
        binding.actvLanguage.setAdapter(adapter)
        binding.actvLanguage.setText("English", false)
    }

    private fun setupCurrencyDropdown() {
        val currencies = arrayOf("LKR", "USD", "EUR", "GBP", "JPY")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, currencies)
        binding.actvCurrency.setAdapter(adapter)
        binding.actvCurrency.setText("LKR", false)
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    // Clear current account
                    accountManager.clearCurrentAccount()
                    // Reset current user without clearing data
                    preferencesManager.setCurrentUser("")
                    findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private data class BackupData(
        val transactions: List<com.example.labexam3.model.Transaction>,
        val monthlyBudget: Double,
        val currency: String,
        val notificationsEnabled: Boolean
    )
} 