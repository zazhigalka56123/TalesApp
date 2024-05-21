package ru.tales.forfamily.presentation.fragments

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.tales.forfamily.R
import ru.tales.forfamily.data.db.TaleRepositoryImpl
import ru.tales.forfamily.data.remote.ApiRepositoryImpl
import ru.tales.forfamily.databinding.FragmentSplashBinding
import ru.tales.forfamily.domain.Tale
import ru.tales.forfamily.presentation.Mode
import ru.tales.forfamily.presentation.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSplash: Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var viewModel: ViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

        lifecycleScope.launch(Dispatchers.IO) {
            val remote = downloadTales()
            val local = getTales(requireActivity().application)
            Log.d("TTT_local", local.size.toString())
            Log.d("TTT_remote", remote?.size.toString())
            val arrayId = local.map { it.id }
            var array = mutableListOf<Tale>()
            if (remote != null){
                remote.forEach {
                    if (arrayId.contains(it.id)){
                        it.isSaved = true
                    }
                    array.add(it)
                }
            }else{
                viewModel.mode.postValue( Mode.NO_INTERNET_MODE)
                array = local.toMutableList()
            }
            Log.d("TTT_arr", array.size.toString())

            var index = 0
            array.forEach {
                it.index = index
                it.text = it.text.replace("<br><br>", "\n")
                index += 1
            }

            viewModel.list.postValue(array)

            withContext(Dispatchers.Main){
                findNavController().navigate(R.id.fragment_main)
            }
        }



        return binding.root
    }

    private suspend fun downloadTales(): List<Tale>? {
        return ApiRepositoryImpl().getTales()
        }
    }
    private suspend fun getTales(application: Application): List<Tale> {
        return TaleRepositoryImpl(application).getTales()
    }