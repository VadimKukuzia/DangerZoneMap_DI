package com.veseleil.dangerzonemap_di.ui.auth.startscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.databinding.FragmentStartScreenBinding
import com.veseleil.dangerzonemap_di.ui.main.MainActivity
import com.veseleil.dangerzonemap_di.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartScreenFragment : Fragment() {

    private var _binding: FragmentStartScreenBinding? = null

    private val binding get() = _binding!!

    private val viewModel: StartScreenViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var oneTapClient: SignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_loginFragment)
        }

        binding.btnRegistration.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_registrationFragment)
        }

        binding.btnGoogleAuth.setOnClickListener {
            startSignInGoogle()
        }

        viewModel.authResponseLiveData.observe(viewLifecycleOwner) { authResponse ->
            if (authResponse.isSuccessful) {
                sessionManager.saveAccessToken(authResponse.body()!!.tokens.accessToken)
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Response Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            handleSignInResult(task)
        } else {
            Log.d("LOGTAG", "RESULT CODE: ${result.resultCode}")
            Log.d("LOGTAG", result.toString())
        }
    }
    private fun startSignInGoogle() {

        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build())
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                resultLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener(requireActivity()) {
                Log.d("LOGTAG", it.localizedMessage!!)
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            Log.d("LOGTAG", account.idToken.toString())
//            apiGoogleAuth(account.idToken.toString())// Signed in successfully, show authenticated UI.
            viewModel.signInGoogle(account.idToken.toString())
//            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LOGTAG", "signInResult:failed code=" + e.statusCode)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}