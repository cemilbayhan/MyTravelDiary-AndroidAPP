package com.example.mytraveldiary.view.app

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytraveldiary.R
import com.example.mytraveldiary.adapter.PlaceCommentAdapter
import com.example.mytraveldiary.adapter.UserCommentAdapter
import com.example.mytraveldiary.databinding.FragmentPlaceCommentsBinding
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.model.NewCommentModel
import com.example.mytraveldiary.service.PlaceCommentListener

import com.example.mytraveldiary.viewModel.PlaceCommentViewModel
import com.google.firebase.database.FirebaseDatabase

class PlaceCommentsFragment : Fragment(), PlaceCommentListener {
    private var _binding: FragmentPlaceCommentsBinding?=null
    private val binding get()=_binding!!
    lateinit var viewModel:PlaceCommentViewModel
    lateinit var placeID:String
    lateinit var placeName:String
    lateinit var placeImage:String
    private val commentAdapter=PlaceCommentAdapter(this,arrayListOf(),this)
    private var myLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentPlaceCommentsBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[PlaceCommentViewModel::class.java]
        arguments?.let {
            placeID=PlaceCommentsFragmentArgs.fromBundle(it).placeID
            placeName=PlaceCommentsFragmentArgs.fromBundle(it).placeName
            placeImage=PlaceCommentsFragmentArgs.fromBundle(it).placeImage
        }
        myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.commentsRecyclerView.layoutManager = myLayoutManager
        binding.commentsRecyclerView.adapter=commentAdapter
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        initializeUI()
        viewModel.getCommentsFromFirebase(placeID)
    }

    private fun initializeUI() {
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        if (viewModel.getUser()){
            viewModel.getUserDetails()
            binding.emptyComment.setOnClickListener {
                viewModel.userDetail.value=NewCommentModel(placeID,placeName,placeImage,viewModel.userName.value!!,viewModel.userImage.value!!)
                CommentFragment().show(childFragmentManager,"newCommenteFragment")
            }
        }else{
            binding.emptyComment.setOnClickListener {
                val alert= AlertDialog.Builder(context)
                alert.setTitle(R.string.needLogin)
                    .setIcon(R.drawable.ic_person_add)
                    .setNeutralButton(R.string.ok) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create().show()
            }
        }

        binding.newCommentText.setOnClickListener {
            viewModel.userDetail.value=NewCommentModel(placeID,placeName,placeImage,viewModel.userName.value!!,viewModel.userImage.value!!)
            CommentFragment().show(childFragmentManager,"newCommenteFragment")
        }

    }

    private fun observeLiveData() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                if (it){
                    binding.commentsProgressBar.visibility=View.VISIBLE
                    binding.newCommentText.visibility=View.GONE
                    binding.emptyComment.visibility=View.GONE
                    binding.commentsRecyclerView.visibility=View.GONE
                }else{
                    binding.commentsProgressBar.visibility=View.GONE
                }
            }
        })
        viewModel.commentCount.observe(viewLifecycleOwner, Observer { count->
            count?.let {
                if(it>0){
                    var string=getString(R.string.commentCount)
                    string= String.format(string, it)
                    binding.commentCountTextView.text=string
                    binding.emptyComment.visibility=View.GONE
                    binding.commentsRecyclerView.visibility=View.VISIBLE
                }else{
                    binding.commentCountTextView.text=getString(R.string.noCommentFound)
                    binding.emptyComment.visibility=View.VISIBLE
                    binding.newCommentText.visibility=View.GONE
                    binding.commentsRecyclerView.visibility=View.GONE
                }
            }
        })
        viewModel.commentList.observe(viewLifecycleOwner, Observer { list->
            list?.let {
                if (it.isEmpty())
                    viewModel.commentError.value=true
                else
                    commentAdapter.updatePlaceList(it)
            }
        })
        viewModel.commentChecker.observe(viewLifecycleOwner, Observer {chkec->
            chkec?.let {
                if (it==1 && viewModel.loading.value == false){
                   binding.newCommentText.visibility=View.GONE
                }
            }
        })
        viewModel.butonEnable.observe(viewLifecycleOwner, Observer { value->
           value?.let {
               if (it)
                   binding.newCommentText.isEnabled=true
           }
        })
    }

    fun recyclerOlustur(newCommentList: ArrayList<Comments>){
        viewModel.commentCount.value=newCommentList.size
        val myLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.commentsRecyclerView.layoutManager = myLayoutManager
        val myAdapter = PlaceCommentAdapter(this,newCommentList,this)
        binding.commentsRecyclerView.adapter = myAdapter

    }

    override fun setVisibility() {
       binding.newCommentText.visibility=View.VISIBLE
    }

    override fun refreshRecycler(newCommentList: ArrayList<Comments>) {
        recyclerOlustur(newCommentList)
    }


}