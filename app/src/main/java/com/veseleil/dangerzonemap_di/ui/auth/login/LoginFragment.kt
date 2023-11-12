package com.veseleil.dangerzonemap_di.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.databinding.FragmentLoginBinding
import com.veseleil.dangerzonemap_di.ui.main.MainActivity
import com.veseleil.dangerzonemap_di.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Injecting
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        insertArguments()

        binding.btnAuthorizeSubmit.setOnClickListener {
            beginAuthorization()
        }

        loginViewModel.authResponseLiveData.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse.isSuccessful) {
                sessionManager.saveAccessToken(authResponse.body()!!.tokens.accessToken)
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Response Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun beginAuthorization() {
        val email = binding.emailTextInputEditText.text.toString()
        val password = binding.passwordTextInputEditText.text.toString()
        if (email.isNotBlank() and password.isNotBlank()) {
            loginViewModel.authorize(email, password)
        }
    }

    private fun insertArguments() {
        if (!arguments?.isEmpty!!) {
            val email = arguments?.getString("email")!!
            binding.emailTextInputEditText.setText(email)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}