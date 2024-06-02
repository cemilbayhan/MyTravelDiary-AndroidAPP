package com.example.mytraveldiary.view.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentSearchBinding
import com.example.mytraveldiary.adapter.PlaceAdapter
import com.example.mytraveldiary.viewModel.SearchViewModel


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private val placeAdapter = PlaceAdapter(arrayListOf())
    var myLayoutManager: LinearLayoutManager? = null
    var query="null"
    var queryRequest="category"
    var flag=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        flag=0
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this)[SearchViewModel::class.java]
        myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        arguments?.let {
            if (SearchFragmentArgs.fromBundle(it).category!="category"){
                query=SearchFragmentArgs.fromBundle(it).category
                queryRequest="category"
            }
        }
        binding.recyclerviewPlace.layoutManager = myLayoutManager
        binding.recyclerviewPlace.adapter = placeAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinner(binding.root.context)
        initializeUI()
        observeLiveData()

    }

    private fun setSpinner(context: Context) {
        val cityArray = resources.getStringArray(R.array.spinnerCity)
        val categoryArray = resources.getStringArray(R.array.spinnerCategory)

        val adapter: ArrayAdapter<String> =
            when(queryRequest) {
            "category" -> ArrayAdapter(context, android.R.layout.simple_spinner_item, categoryArray)
            "city" -> ArrayAdapter(context, android.R.layout.simple_spinner_item, cityArray)
            else -> ArrayAdapter(context, android.R.layout.simple_spinner_item, categoryArray)
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.searchView.adapter = adapter
    }

    private fun initializeUI() {
        binding.searchView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (flag==1)
                    query = parent!!.getItemAtPosition(position).toString()
                when(query){
                    "Tarihi Alanlar"->binding.searchView.setSelection(1)
                    "Doğal Alanlar"->binding.searchView.setSelection(2)
                    "Sahil ve Kumsallar"->binding.searchView.setSelection(3)
                    "Müzeler"->binding.searchView.setSelection(4)
                }
                flag=1
                when(queryRequest){
                    "category"-> viewModel.getDataFromCategory(query)
                    "city"    -> viewModel.getDataFromCity(query)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.filterImage.setOnClickListener {
            val options = arrayOf(getString(R.string.category), getString(R.string.city))
            var selectedOption = 0
            AlertDialog.Builder(binding.root.context)
                .setTitle(getString(R.string.filtreOptions))
                .setSingleChoiceItems(options, selectedOption) { _, which ->
                    selectedOption = which
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    when(options[selectedOption]){
                        "Kategoriye göre ara" -> {
                            queryRequest="category"
                            setSpinner(binding.root.context)
                        }
                        "Şehir ismine göre ara"-> {
                            queryRequest="city"
                            setSpinner(binding.root.context)
                        }
                    }
                }
                .create().show()
        }
    }
    private fun observeLiveData() {
        viewModel.placeLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (it) {
                    binding.recyclerviewPlace.visibility = View.GONE
                    binding.searchProgressBar.visibility = View.VISIBLE
                } else {
                    binding.searchProgressBar.visibility = View.GONE
                }
            }
        })

        viewModel.placeList.observe(viewLifecycleOwner, Observer { places ->
            places?.let {
                if (it.isEmpty())
                    viewModel.placeError.value=true
                else{
                    binding.recyclerviewPlace.visibility = View.VISIBLE
                    placeAdapter.updatePlaceList(places)
                }

            }
        })

        viewModel.placeError.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    binding.searchFailedTextView.visibility = View.VISIBLE
                    binding.recyclerviewPlace.visibility = View.GONE
                } else {
                    binding.recyclerviewPlace.visibility = View.VISIBLE
                    binding.searchFailedTextView.visibility = View.GONE
                }
            }
        })
    }
}