package com.example.mytraveldiary.view.splashAndOnBoard.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentFifthScreenBinding


class FifthScreen : Fragment() {
    private var _binding:FragmentFifthScreenBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentFifthScreenBinding.inflate(inflater,container,false)

        binding.btnFinish.setOnClickListener {
            val sharedPref =
                requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("Finished", true)
            editor.apply()
            findNavController().navigate(R.id.action_onBoardingFragment_to_mainFragment)
        }

        return binding.root
    }


}