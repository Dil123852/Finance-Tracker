package com.example.labexam3.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labexam3.R
import com.example.labexam3.databinding.FragmentLoginBinding
import com.example.labexam3.utils.AccountManager
import com.example.labexam3.utils.PreferencesManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var accountManager: AccountManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        accountManager = AccountManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                val username = binding.tilUsername.editText?.text.toString()
                val password = binding.tilPassword.editText?.text.toString()
                
                val account = accountManager.getAccount(username)
                if (account != null && account.password == password) {
                    // Set current account in AccountManager
                    accountManager.setCurrentAccount(account)
                    // Set current user in PreferencesManager
                    preferencesManager.setCurrentUser(username)
                    // Don't reset any preferences, just use existing ones
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate username
        if (binding.tilUsername.editText?.text.isNullOrEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        // Validate password
        if (binding.tilPassword.editText?.text.isNullOrEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 