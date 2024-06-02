package com.example.mytraveldiary.view.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytraveldiary.R
import com.example.mytraveldiary.adapter.FavAdapter
import com.example.mytraveldiary.adapter.UserCommentAdapter
import com.example.mytraveldiary.databinding.FragmentUserCommentsBinding
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.model.PlaceModel
import com.example.mytraveldiary.viewModel.UserCommentViewModel

class UserCommentsFragment : Fragment() {
    private var _binding:FragmentUserCommentsBinding?=null
    private val binding get()=_binding!!
    lateinit var viewModel:UserCommentViewModel
    lateinit var userID:String
    private val commentAdapter=UserCommentAdapter(this,arrayListOf())
    var myLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentUserCommentsBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[UserCommentViewModel::class.java]
        arguments?.let {
            userID=UserCommentsFragmentArgs.fromBundle(it).userUID
        }
        myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.commentsRecyclerView.layoutManager = myLayoutManager
        binding.commentsRecyclerView.adapter=commentAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        observeLiveData(view)
        viewModel.getCommentsFromFirebase(userID)
    }

    private fun initializeUI() {
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun observeLiveData(view: View) {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                //TODO Yüklenirken olacakları ayarla
            }
        })
        viewModel.commentCount.observe(viewLifecycleOwner, Observer { count->
            count?.let {
                if(it>0){
                    var string=getString(R.string.commentCount)
                    string= String.format(string, it)
                    binding.commentCountTextView.text=string
                    binding.commentsRecyclerView.visibility=View.VISIBLE
                }else{
                    binding.commentCountTextView.text=getString(R.string.noCommentFound)
                    binding.commentsRecyclerView.visibility=View.GONE
                }
            }
        })
        viewModel.commentList.observe(viewLifecycleOwner, Observer { list->
            list?.let {
                if (it.isEmpty())
                    viewModel.commentError.value=true
                else
                    commentAdapter.updateCommentList(it)
            }
        })
    }

    fun recyclerOlustur(newCommentList: ArrayList<Comments>){
        viewModel.commentCount.value=newCommentList.size
        val myLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.commentsRecyclerView.layoutManager = myLayoutManager
        val myAdapter = UserCommentAdapter(this,newCommentList)
        binding.commentsRecyclerView.adapter = myAdapter

    }
}