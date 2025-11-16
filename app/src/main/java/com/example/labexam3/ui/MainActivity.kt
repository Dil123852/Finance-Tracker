package com.example.labexam3.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.labexam3.R
import com.example.labexam3.databinding.ActivityMainBinding
import com.example.labexam3.ui.auth.LoginActivity
import com.example.labexam3.utils.NotificationHelper
import com.example.labexam3.utils.PreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferencesManager = PreferencesManager(this)
        
        // Check if user is logged in
        if (preferencesManager.getCurrentUser().isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationHelper = NotificationHelper(this)

        // Get the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up bottom navigation
        bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)

        // Set theme and bottom navigation visibility based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.landingFragment, R.id.loginFragment, R.id.signupFragment -> {
                    setTheme(R.style.Theme_LabExam3_NoActionBar)
                    bottomNav.visibility = View.GONE
                }
                else -> {
                    setTheme(R.style.Theme_LabExam3)
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return false
    }

    companion object {
        const val TAG = "MainActivity"
    }
} 