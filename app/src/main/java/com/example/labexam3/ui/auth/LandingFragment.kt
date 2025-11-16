package com.example.labexam3.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labexam3.R
import com.example.labexam3.databinding.FragmentLandingBinding

class LandingFragment : Fragment() {

    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Navigate to login after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                findNavController().navigate(R.id.action_landingFragment_to_loginFragment)
            } catch (e: Exception) {
                // Handle any navigation errors silently
            }
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 