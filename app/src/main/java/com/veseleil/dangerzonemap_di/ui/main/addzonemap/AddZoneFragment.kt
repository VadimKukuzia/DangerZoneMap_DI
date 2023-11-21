package com.veseleil.dangerzonemap_di.ui.main.addzonemap

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.databinding.FragmentAddZoneBinding
import com.veseleil.dangerzonemap_di.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddZoneFragment : Fragment() {

    private lateinit var _binding: FragmentAddZoneBinding
    private val binding get() = _binding

    private lateinit var viewModel: AddZoneViewModel

    @Inject
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddZoneBinding.inflate(inflater, container, false)
        return binding.root
    }

}