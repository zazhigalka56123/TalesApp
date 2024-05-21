package ru.tales.forfamily.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.tales.forfamily.databinding.FragmentPayBinding
import ru.tales.forfamily.presentation.ViewModel

class FragmentPaywall: Fragment() {
    private lateinit var binding: FragmentPayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
//        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        binding = FragmentPayBinding.inflate(inflater, container, false)
        var viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]


        binding.close.setOnClickListener {
            viewModel.backPay = true
            viewModel.stop = false
            findNavController().popBackStack()
        }


        return binding.root
    }
}