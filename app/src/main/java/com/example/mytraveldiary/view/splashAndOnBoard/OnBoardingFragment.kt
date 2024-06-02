package com.example.mytraveldiary.view.splashAndOnBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mytraveldiary.view.splashAndOnBoard.screens.FirstScreen
import com.example.mytraveldiary.view.splashAndOnBoard.screens.SecondScreen
import com.example.mytraveldiary.databinding.FragmentOnBoardingBinding
import com.example.mytraveldiary.adapter.OnBoardAdapter
import com.example.mytraveldiary.view.splashAndOnBoard.screens.FifthScreen
import com.example.mytraveldiary.view.splashAndOnBoard.screens.FourthScreen
import com.example.mytraveldiary.view.splashAndOnBoard.screens.ThirdScreen


class OnBoardingFragment : Fragment() {
    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get()= _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(), SecondScreen(), ThirdScreen(), FourthScreen(),FifthScreen()
        )
        val adapter = OnBoardAdapter(
            fragmentList, requireActivity().supportFragmentManager, lifecycle
        )
        binding.onBoardingViewPager.adapter = adapter

        return binding.root
    }


}