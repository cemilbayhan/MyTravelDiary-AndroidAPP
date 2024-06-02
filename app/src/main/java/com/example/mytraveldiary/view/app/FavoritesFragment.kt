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
import com.example.mytraveldiary.databinding.FragmentFavoritesBinding
import com.example.mytraveldiary.model.PlaceModel
import com.example.mytraveldiary.adapter.FavAdapter
import com.example.mytraveldiary.viewModel.FavoritesViewModel


class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding?=null
    private val binding get()=_binding!!
    lateinit var viewModel: FavoritesViewModel
    private val favAdapter= FavAdapter(this, arrayListOf())
    var myLayoutManager: LinearLayoutManager?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentFavoritesBinding.inflate(inflater,container,false)
        viewModel= ViewModelProviders.of(this)[FavoritesViewModel::class.java]
        viewModel.loading.value=true
        myLayoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.favRecyclerView.layoutManager=myLayoutManager
        binding.favRecyclerView.adapter=favAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(view)
        viewModel.getDatas()
    }

    private fun observeLiveData(view: View) {
        viewModel.loading.observe(viewLifecycleOwner, Observer {loading->
            loading?.let {
                if(it){
                    binding.emptyGroupTextView.visibility=View.GONE
                    binding.favGroupTextView.visibility=View.GONE
                    binding.favoritesProgressBar.visibility=View.VISIBLE
                }else{
                    binding.favoritesProgressBar.visibility=View.GONE
                    if(viewModel.itemCount.value==0){
                        binding.emptyFav.visibility=View.VISIBLE
                        binding.errorFav.visibility=View.GONE
                        binding.favGroupTextView.visibility=View.GONE
                    }else{
                        var string=getString(R.string.favCount)
                        string= String.format(string,viewModel.itemCount.value)
                        binding.favCountTextView.text=string
                        binding.favGroupTextView.visibility=View.VISIBLE
                        binding.emptyGroupTextView.visibility=View.GONE
                    }
                }
            }
        })
        viewModel.itemCount.observe(viewLifecycleOwner, Observer { count->
            count?.let {
                if(it>0){
                    binding.emptyFav.visibility=View.GONE
                    var string=getString(R.string.favCount)
                    string= String.format(string, viewModel.itemCount.value)
                    binding.favCountTextView.text=string
                    binding.favCountTextView.visibility=View.VISIBLE
                }
                else if(viewModel.loading.value==false){
                    binding.favCountTextView.visibility=View.GONE
                    binding.emptyFav.visibility=View.VISIBLE
                }
            }
        })

        viewModel.places.observe(viewLifecycleOwner, Observer { places->
            places?.let {
                if(viewModel.itemCount.value!!>0)
                    favAdapter.updateFavList(it)
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error->
            error?.let {
                if (it){
                    binding.favGroupTextView.visibility=View.GONE
                    binding.emptyGroupTextView.visibility=View.VISIBLE
                }
            }
        })
    }

    fun recyclerOlustur(newFavList: ArrayList<PlaceModel>){
        viewModel.itemCount.value=newFavList.size
        val myLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favRecyclerView.layoutManager = myLayoutManager
        val myAdapter = FavAdapter(this,newFavList)
        binding.favRecyclerView.adapter = myAdapter

    }


}