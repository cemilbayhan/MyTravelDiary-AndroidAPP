package com.example.mytraveldiary.view.splashAndOnBoard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding?=null
    private val binding get()= _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentSplashBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animation()
    }

    private fun animation() {
        val slideAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.side_slide)
        binding.imgvSplash.startAnimation(slideAnimation)

        slideAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                if (onBoardingFinished()){
                    val action = SplashFragmentDirections.actionSplashFragmentToMainFragment()
                    Navigation.findNavController(binding.root).navigate(action)
                }else{
                    val action = SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment()
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

}