package com.example.mytraveldiary.view.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentCommentBinding
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.viewModel.NewCommentViewModel
import com.example.mytraveldiary.viewModel.PlaceCommentViewModel


class CommentFragment : DialogFragment() {
    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel:NewCommentViewModel
    private lateinit var viewModelx: PlaceCommentViewModel
    lateinit var placeID:String
    lateinit var placeName:String
    lateinit var placeImage:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentCommentBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[NewCommentViewModel::class.java]
        viewModelx=ViewModelProviders.of(requireParentFragment())[PlaceCommentViewModel::class.java]
        viewModelx.getUserDetails()
        viewModel.findNewID() // Yeni yorum için olmayan bir id buldurma işlemi başlatılıyor.
        placeID=viewModelx.userDetail.value!!.placeID
        placeImage=viewModelx.userDetail.value!!.placeImage
        placeName=viewModelx.userDetail.value!!.placeName
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loading.value=true
        viewModel.rate.value=5
        initializeUI()
        observeLiveData()
    }

    override fun dismiss() {
        super.dismiss()
        viewModelx.getCommentsFromFirebase(placeID)
    }

    private fun observeLiveData() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                if(it){
                    binding.newCommentGroupView.visibility=View.GONE
                    binding.newCommentProgressBar.visibility=View.VISIBLE
                }else{
                    binding.newCommentGroupView.visibility=View.VISIBLE
                    binding.newCommentProgressBar.visibility=View.GONE
                }

            }
        })
        viewModel.rate.observe(viewLifecycleOwner, Observer { rate->
            rate?.let {
                val starList= arrayListOf(binding.starOne,binding.starTwo,binding.starThree,binding.starFour,binding.starFive)
                for (a in it until 5){
                    starList[a].setImageResource(R.drawable.ic_starborder)
                }
                for (a in it downTo  1){
                    starList[a-1].setImageResource(R.drawable.ic_starfull)
                }
            }
        })
        viewModel.commentSuccess.observe(viewLifecycleOwner, Observer {success->
            success?.let {
                if(it=="True"){
                    dismiss()
                    Toast.makeText(context,R.string.commentSuccess,Toast.LENGTH_LONG).show()
                }else
                    Toast.makeText(context,it,Toast.LENGTH_LONG).show()

            }
        })
    }

    private fun initializeUI() {
        setComponents()
        binding.starOne.setOnClickListener { viewModel.rate.value=1 }
        binding.starTwo.setOnClickListener { viewModel.rate.value=2 }
        binding.starThree.setOnClickListener { viewModel.rate.value=3 }
        binding.starFour.setOnClickListener { viewModel.rate.value=4 }
        binding.starFive.setOnClickListener { viewModel.rate.value=5 }
        binding.closeFragment.setOnClickListener {
            dialog!!.cancel()
        }
        binding.closeOneFragment.setOnClickListener {
            dialog!!.cancel()
        }
        binding.sendLink.setOnClickListener {
            viewModel.setNewComment(viewModelx.userDetail.value!!.placeID,viewModelx.userDetail.value!!.placeName,viewModelx.userDetail.value!!.placeImage,viewModelx.userDetail.value!!.userName,viewModelx.userDetail.value!!.userImage,binding.userComment.text.toString())
        }
    }

    private fun setComponents() {
        binding.placeNameTextView.text=placeName
        binding.userProfileImage.getImage(viewModelx.userImage.value!!, progressDrawable(binding.root.context))
        binding.userNameTextView.text=viewModelx.userName.value
        viewModel.loading.value=false
    }


}