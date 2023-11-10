package com.veseleil.dangerzonemap_di.ui.auth.startscreen

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.databinding.FragmentStartScreenBinding
import com.veseleil.dangerzonemap_di.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartScreenFragment : Fragment() {

    private var _binding: FragmentStartScreenBinding? = null

    private val binding get() = _binding!!

    private val startScreenViewModel: StartScreenViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MyTag", "StartScreen Session manager: $sessionManager")

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_loginFragment)
        }

        binding.btnRegistration.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_registrationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}