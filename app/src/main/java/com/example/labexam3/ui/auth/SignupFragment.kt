package com.example.labexam3.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labexam3.R
import com.example.labexam3.databinding.FragmentSignupBinding
import com.example.labexam3.utils.AccountManager
import com.example.labexam3.utils.PreferencesManager

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var accountManager: AccountManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        accountManager = AccountManager(requireContext())
        preferencesManager = PreferencesManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.text.toString()
                val age = binding.etAge.text.toString()
                val username = binding.etUsername.text.toString()
                val password = binding.etPassword.text.toString()

                if (accountManager.getAccount(username) != null) {
                    binding.tilUsername.error = "Username already taken"
                } else {
                    val account = AccountManager.Account(
                        username = username,
                        password = password,
                        email = "", // You can add email field if needed
                        phone = "",  // You can add phone field if needed
                        name = name,
                        age = age // Keep as String to match Account class
                    )
                    accountManager.saveAccount(account)
                    preferencesManager.setCurrentUser(username)
                    findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate name
        if (binding.etName.text.isNullOrEmpty()) {
            binding.tilName.error = "Name is required"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        // Validate age
        if (binding.etAge.text.isNullOrEmpty()) {
            binding.tilAge.error = "Age is required"
            isValid = false
        } else {
            binding.tilAge.error = null
        }

        // Validate username
        if (binding.etUsername.text.isNullOrEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        // Validate password
        if (binding.etPassword.text.isNullOrEmpty()) {
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