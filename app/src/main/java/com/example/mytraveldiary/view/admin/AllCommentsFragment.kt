package com.example.mytraveldiary.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytraveldiary.R
import com.example.mytraveldiary.adapter.AllCommentsAdapter
import com.example.mytraveldiary.adapter.UserCommentAdapter
import com.example.mytraveldiary.databinding.FragmentAllCommentsBinding
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.viewModel.AllCommentsViewModel

class AllCommentsFragment : Fragment() {
    private var _binding:FragmentAllCommentsBinding?=null
    private val binding get()=_binding!!
    lateinit var viewModel:AllCommentsViewModel
    private val commentAdapter=AllCommentsAdapter(this, arrayListOf())
    var myLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentAllCommentsBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[AllCommentsViewModel::class.java]
        myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.allCommentsRecyclerView.layoutManager = myLayoutManager
        binding.allCommentsRecyclerView.adapter=commentAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                //TODO Yüklenirken olacakları ayarla
            }
        })
        viewModel.commentCount.observe(viewLifecycleOwner, Observer { count->
            count?.let {
                if(it>0){
                    binding.emptyCommentsTextView.visibility=View.GONE
                    binding.allCommentsRecyclerView.visibility=View.VISIBLE
                }else{
                    binding.emptyCommentsTextView.visibility=View.VISIBLE
                    binding.allCommentsRecyclerView.visibility=View.GONE
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

    private fun initializeUI() {
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.getCommentsFromFirebase()
    }

    fun recyclerOlustur(newCommentList: ArrayList<Comments>){
        viewModel.commentCount.value=newCommentList.size
        val myLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.allCommentsRecyclerView.layoutManager = myLayoutManager
        val myAdapter = AllCommentsAdapter(this,newCommentList)
        binding.allCommentsRecyclerView.adapter = myAdapter

    }

}