package ru.tales.forfamily.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.tales.forfamily.App
import ru.tales.forfamily.databinding.FragmentMainBinding
import ru.tales.forfamily.domain.Tale
import ru.tales.forfamily.domain.player.PlayerService
import ru.tales.forfamily.presentation.Mode
import ru.tales.forfamily.presentation.ViewModel
import ru.tales.forfamily.presentation.recyclerview.Adapter


class FragmentMain: Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val taleAdapter = Adapter()
    private var array: List<Tale> = emptyList()
    private lateinit var viewModel: ViewModel

    override fun onResume() {
        super.onResume()
        PlayerService.exoPlayer?.play()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

        binding.ll.children.forEach {
            it is ViewGroup
        }

        setupRecyclerView()
        updateMode()

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            }
        )


        viewModel.list.observeForever {
            taleAdapter.submitList(it)
            Log.d("TTT", it.size.toString())
            array = it

        }

        viewModel.mode.observeForever {
            updateMode()
        }


        return binding.root
    }



    private fun updateMode(){
        if (viewModel.mode.value == Mode.NO_INTERNET_MODE) {
            binding.cardNoWifi.visibility = View.VISIBLE

            with(viewModel.list.value) {
                if (!this.isNullOrEmpty()) {
                    array = this
                    taleAdapter.submitList(this)
                    binding.tvNoDownloadedTales.visibility = View.GONE
                    binding.tvToLoadTales.visibility = View.GONE
                } else {
                    binding.tvNoDownloadedTales.visibility = View.VISIBLE
                    binding.tvToLoadTales.visibility = View.VISIBLE

                }
            }
        }
    }

    private fun setupRecyclerView(){
        with(binding.recyclerView) {
            adapter = taleAdapter
        }
        taleAdapter.submitList(viewModel.list.value)

        taleAdapter.click = { tale ->
            var stop = true
            if (PlayerService.exoPlayer != null && PlayerService.exoPlayer!!.mediaMetadata.title == tale.name){
                stop = false
            }

            App.tales = array
            findNavController()
                .navigate(
                    FragmentMainDirections.actionFragmentMainToFragmentPlayer(
                        tale.index,
                        stop
                    )
            )
        }
    }
}