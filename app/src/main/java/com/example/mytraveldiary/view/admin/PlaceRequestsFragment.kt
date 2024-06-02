package com.example.mytraveldiary.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytraveldiary.adapter.AdminPlaceRequestsAdapter

import com.example.mytraveldiary.databinding.FragmentPlaceRequestsBinding
import com.example.mytraveldiary.viewModel.PlaceRequestsViewModel

class PlaceRequestsFragment : Fragment() {
    private var _binding: FragmentPlaceRequestsBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: PlaceRequestsViewModel
    private val placeRequestAdapter= AdminPlaceRequestsAdapter(this, arrayListOf())
    var myLayoutManager: LinearLayoutManager?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentPlaceRequestsBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[PlaceRequestsViewModel::class.java]
        myLayoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.placeRequestRecyclerView.layoutManager=myLayoutManager
        binding.placeRequestRecyclerView.adapter=placeRequestAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDatas()
        initializeUI()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                if (it){
                    binding.placeRequestRecyclerView.visibility=View.GONE
                    binding.placeRequestProgressBar.visibility=View.VISIBLE
                }else{
                    binding.placeRequestProgressBar.visibility=View.GONE
                }
            }
        })
        viewModel.itemCount.observe(viewLifecycleOwner, Observer { count->
            count?.let {
                if (it==0 && viewModel.loading.value==false){
                    binding.placeRequestRecyclerView.visibility=View.GONE
                    binding.emptyTextView.visibility=View.VISIBLE
                }else{
                    binding.emptyTextView.visibility=View.GONE
                }
            }
        })
        viewModel.requests.observe(viewLifecycleOwner, Observer { request->
            request?.let {
                if (viewModel.itemCount.value!!>0){
                    placeRequestAdapter.updatePlaceList(it)
                    binding.emptyTextView.visibility=View.GONE
                    binding.placeRequestRecyclerView.visibility=View.VISIBLE
                }
            }
        })
    }

    private fun initializeUI() {
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}