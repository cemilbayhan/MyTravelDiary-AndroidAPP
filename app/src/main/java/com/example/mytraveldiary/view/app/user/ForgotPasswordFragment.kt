package com.example.mytraveldiary.view.app.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordFragment : DialogFragment() {
    private var _binding: FragmentForgotPasswordBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentForgotPasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeFragment.setOnClickListener {
            dismiss()
        }
        binding.sendLink.setOnClickListener {
            if(binding.emailForgot.text.toString()!="")
                sifreSifirlaBaglantisiYolla()
            else
                Toast.makeText(activity,R.string.email,Toast.LENGTH_SHORT).show()
        }
    }

    private fun sifreSifirlaBaglantisiYolla() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.emailForgot.text.toString())
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(activity,R.string.checkMail,Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                else
                    Toast.makeText(activity,"${R.string.error}="+task.exception?.message,Toast.LENGTH_SHORT).show()

            }
    }


}