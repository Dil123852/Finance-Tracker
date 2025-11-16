package com.example.labexam3.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam3.databinding.ActivityLoginBinding
import com.example.labexam3.ui.MainActivity
import com.example.labexam3.utils.PreferencesManager
import com.example.labexam3.utils.AccountManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        accountManager = AccountManager(this)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            
            val account = accountManager.getAccount(username)
            if (account != null && account.password == password) {
                // Set current account in AccountManager
                accountManager.setCurrentAccount(account)
                // Set current user in PreferencesManager
                preferencesManager.setCurrentUser(username)
                // Don't reset any preferences, just use existing ones
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 