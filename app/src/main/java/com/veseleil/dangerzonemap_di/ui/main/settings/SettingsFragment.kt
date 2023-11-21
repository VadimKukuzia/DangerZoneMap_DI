package com.veseleil.dangerzonemap_di.ui.main.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.databinding.FragmentSettingsBinding
import com.veseleil.dangerzonemap_di.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var _binding: FragmentSettingsBinding
    private val binding get() = _binding

    private val zoneTypesSet = mutableSetOf("NT", "TG", "AG", "EC")

    @Inject
    private lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO MADE GOOGLE CLIENT AND OPTIONS HILT DEPENDENCY

//        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.web_client_id))
//            .requestEmail()
//            .build()
//
//        gsc = GoogleSignIn.getClient(requireContext(), gso)

        sessionManager.putZoneTypes(zoneTypesSet)

        binding.cbNatural.isChecked = sessionManager.fetchZoneTypes()?.contains("NT") == true
        binding.cbEcological.isChecked = sessionManager.fetchZoneTypes()?.contains("EC") == true
        binding.cbTechnogenic.isChecked = sessionManager.fetchZoneTypes()?.contains("TG") == true
        binding.cbAnthropogenic.isChecked = sessionManager.fetchZoneTypes()?.contains("AG") == true

        binding.cbAnthropogenic.setOnCheckedChangeListener { compoundButton, b ->
            onCheckBoxClick(compoundButton)
        }
        binding.cbNatural.setOnCheckedChangeListener { compoundButton, b ->
            onCheckBoxClick(compoundButton)
        }
        binding.cbEcological.setOnCheckedChangeListener { compoundButton, b ->
            onCheckBoxClick(compoundButton)
        }
        binding.cbTechnogenic.setOnCheckedChangeListener { compoundButton, b ->
            onCheckBoxClick(compoundButton)
        }

        binding.btnLogout.setOnClickListener {
            startLogOut()
        }
    }

    private fun startLogOut() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.logout_alert_dialog_title)
            setMessage(R.string.logout_alert_dialog_message)
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { _, _ ->
                    logOut()
                }
            )
            setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
            )
            create()
            show()
        }
    }

    private fun logOut() {
        sessionManager.deleteAccessToken()
        gsc.signOut()
        activity?.recreate()
    }

    private fun onCheckBoxClick(cb: View?) {

        cb as CheckBox
        val isChecked = cb.isChecked
        when (cb.id) {
            R.id.cb_natural -> {
                if (isChecked) {
                    zoneTypesSet.add("NT")
                } else {
                    zoneTypesSet.remove("NT")
                }
            }
            R.id.cb_ecological -> {
                if (isChecked) {
                    zoneTypesSet.add("EC")
                } else {
                    zoneTypesSet.remove("EC")
                }
            }
            R.id.cb_anthropogenic -> {
                if (isChecked) {
                    zoneTypesSet.add("AG")
                } else {
                    zoneTypesSet.remove("AG")
                }
            }
            R.id.cb_technogenic -> {
                if (isChecked) {
                    zoneTypesSet.add("TG")
                } else {
                    zoneTypesSet.remove("TG")
                }
            }
        }
        sessionManager.putZoneTypes(zoneTypesSet)
    }

}