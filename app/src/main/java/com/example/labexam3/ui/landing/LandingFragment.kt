package com.example.labexam3.ui.landing

import android.os.Bundle
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
        
        // Navigate to login after a short delay
        view.postDelayed({
            findNavController().navigate(R.id.action_landingFragment_to_loginFragment)
        }, 2000) // 2 seconds delay
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 