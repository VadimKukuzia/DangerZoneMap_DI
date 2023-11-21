package com.veseleil.dangerzonemap_di.ui.auth.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.veseleil.dangerzonemap_di.databinding.FragmentRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegistrationSubmit.setOnClickListener {
            beginRegistration()
        }

        viewModel.registrationResponseLiveData.observe(viewLifecycleOwner) { registrationResponse ->
            if (registrationResponse.isSuccessful) {
                val direction = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment(
                    registrationResponse.body()!!.email)
                findNavController().navigate(direction)
            } else {
                Toast.makeText(requireContext(), "Response Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun beginRegistration() {
        val username = binding.usernameTextInputEditText.text.toString()
        val email = binding.emailTextInputEditText.text.toString()
        val password = binding.passwordTextInputEditText.text.toString()

        if (username.isNotBlank() and email.isNotBlank() and password.isNotBlank()) {
            viewModel.register(username, email, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}