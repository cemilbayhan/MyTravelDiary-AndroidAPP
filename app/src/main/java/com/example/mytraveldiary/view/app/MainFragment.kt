package com.example.mytraveldiary.view.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentMainBinding
import com.example.mytraveldiary.service.MainListener
import com.example.mytraveldiary.viewModel.MainViewModel


class MainFragment : Fragment() {
    private var _binding:FragmentMainBinding?=null
    private val binding get()=_binding!!
    lateinit var activityListener: MainListener
    lateinit var viewModel:MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentMainBinding.inflate(inflater,container,false)
        activityListener = activity as MainListener
        activityListener.showOrHide(true)
        viewModel=ViewModelProviders.of(this)[MainViewModel::class.java]
        //TODO API VERİ ÇEKME İŞLEMİ BURADA
        viewModel.checkDatabase()
        initializeUI(binding.root)
        return binding.root
    }

    private fun initializeUI(view: View) {
        binding.museumImageButton.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Müzeler")
            Navigation.findNavController(view).navigate(action)
        }
        binding.museumTextView.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Müzeler")
            Navigation.findNavController(view).navigate(action)
        }
        binding.forestImageButton.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Tarihi Alanlar")
            Navigation.findNavController(view).navigate(action)
        }
        binding.forestTextView.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Tarihi Alanlar")
            Navigation.findNavController(view).navigate(action)
        }
        binding.campingImageButton.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Doğal Alanlar")
            Navigation.findNavController(view).navigate(action)
        }
        binding.campingTextView.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Doğal Alanlar")
            Navigation.findNavController(view).navigate(action)
        }
        binding.beachImageButton.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Sahil ve Kumsallar")
            Navigation.findNavController(view).navigate(action)
        }
        binding.beachTextView.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToSearchFragment("Sahil ve Kumsallar")
            Navigation.findNavController(view).navigate(action)
        }
    }




}