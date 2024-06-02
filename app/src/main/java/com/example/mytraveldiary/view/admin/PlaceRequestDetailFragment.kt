package com.example.mytraveldiary.view.admin

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytraveldiary.view.admin.PlaceRequestDetailFragmentArgs
import com.example.mytraveldiary.view.admin.PlaceRequestDetailFragmentDirections
import com.example.mytraveldiary.R
import com.example.mytraveldiary.adapter.ImageRecyclerAdapter
import com.example.mytraveldiary.databinding.FragmentPlaceRequestDetailBinding
import com.example.mytraveldiary.databinding.FragmentPlaceRequestsBinding
import com.example.mytraveldiary.service.ImageClickListener
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.viewModel.PlaceRequestDetailViewModel

class PlaceRequestDetailFragment : Fragment(), ImageClickListener {
    private var _binding:FragmentPlaceRequestDetailBinding?=null
    private val binding get()=_binding!!
    lateinit var viewModel:PlaceRequestDetailViewModel
    lateinit var requestID:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            requestID= PlaceRequestDetailFragmentArgs.fromBundle(it).requestID
        }
        _binding= FragmentPlaceRequestDetailBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[PlaceRequestDetailViewModel::class.java]
        viewModel.findNewID()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        observeLiveData()
        viewModel.getDatas(requestID)
        binding.imagesRecyclerView.layoutManager=
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun observeLiveData() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                enableDisableComponents(it)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(context,it, Toast.LENGTH_LONG).show()
            }

        })
        viewModel.placeDetails.observe(viewLifecycleOwner, Observer { details ->
            details?.let {
                binding.placeNameText.text=it.placeName
                binding.placeDescription.text=it.description
                binding.placePrice.text=it.price
                binding.placeAddress.text=it.address
                val string=", "+it.city
                binding.placeCityText.text=string
                binding.viewPager.getImage(it.images[0], progressDrawable(requireContext()))
                binding.imagesRecyclerView.adapter= ImageRecyclerAdapter(it.images,this)
            }
        })
        viewModel.updateSuccess.observe(viewLifecycleOwner, Observer { success->
            success?.let {
                if (it=="True"){
                    viewModel.loading.value=false
                    Toast.makeText(binding.root.context,R.string.success,Toast.LENGTH_SHORT).show()
                    val action= PlaceRequestDetailFragmentDirections.actionPlaceRequestDetailFragmentToAdminMainFragment()
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }
        })
    }

    private fun enableDisableComponents(it: Boolean) {
        if (it)
            binding.mainConstraint.visibility=View.GONE
        else
            binding.mainConstraint.visibility=View.VISIBLE
    }

    private fun initializeUI() {
        binding.placeDescription.movementMethod = ScrollingMovementMethod()
        binding.acceptBTN.setOnClickListener {
            viewModel.addDatabase(requestID)
        }
        binding.declineBTN.setOnClickListener {
            viewModel.removeDatabase(requestID)
        }
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun imageClickListener(position: Int) {
        setImage(position)
    }

    private fun setImage(position: Int) {
        binding.viewPager.getImage(viewModel.placeDetails.value!!.images[position], progressDrawable(requireContext()))
    }


}