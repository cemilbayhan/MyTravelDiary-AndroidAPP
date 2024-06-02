package com.example.mytraveldiary.view.app.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentLoginBinding
import com.example.mytraveldiary.model.User
import com.example.mytraveldiary.viewModel.LoginViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding?=null
    private val binding get()=_binding!!
    lateinit var viewModel:LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentLoginBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[LoginViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        observeLiveData(view)
    }

    private fun initializeUI() {
        binding.signUpTextView.setOnClickListener {
            val action= LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        binding.signInButton.setOnClickListener {
            val userEmail=binding.emailText.text.toString()
            val userPassword=binding.passwordLogin.text.toString()
            if(userEmail!="" && userPassword!="") {
                val userInformation = User(userEmail, userPassword)
                viewModel.signInWithFirebase(userInformation)
            }
            else
                Toast.makeText(context, R.string.fill,Toast.LENGTH_LONG).show()

        }
        binding.forgotPasswordTextView.setOnClickListener {
            ForgotPasswordFragment().show(childFragmentManager,"forgotPasswordFragment")
        }
    }

    private fun observeLiveData(view: View) {
        viewModel.loginIsSuccess.observe(viewLifecycleOwner, Observer { isSuccess->
            isSuccess?.let {
                if(it){
                    val action= LoginFragmentDirections.actionLoginFragmentToProfileFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        })
        viewModel.loginErrorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(context,error,Toast.LENGTH_LONG).show()
            }
        })
        viewModel.loginInProgress.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if(it) enableDisableComponents(false)
                else enableDisableComponents(true)
            }
        })
        viewModel.loginIsAdmin.observe(viewLifecycleOwner, Observer { admin->
            admin?.let {
                if (it){
                    val action=LoginFragmentDirections.actionLoginFragmentToAdminMainFragment()
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }
        })
    }

    private fun enableDisableComponents(value: Boolean) {
        binding.signInButton.isEnabled=value
        binding.emailText.isEnabled=value
        binding.passwordLogin.isEnabled=value
        binding.signUpTextView.isClickable=value
        binding.forgotPasswordTextView.isClickable=value
        if(value) binding.loginProgressBar.visibility=View.GONE
        else binding.loginProgressBar.visibility=View.VISIBLE

    }


}