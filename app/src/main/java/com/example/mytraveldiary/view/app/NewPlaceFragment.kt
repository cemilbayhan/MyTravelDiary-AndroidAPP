package com.example.mytraveldiary.view.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentNewPlaceBinding
import com.example.mytraveldiary.viewModel.NewPlaceViewModel

class NewPlaceFragment : Fragment() {
    private var _binding: FragmentNewPlaceBinding?=null
    private val binding get() = _binding!!
    lateinit var viewModel:NewPlaceViewModel
    lateinit var userName:String
    lateinit var userUID:String
    lateinit var userImage:String
    lateinit var placeCategory:String
    lateinit var placeCity:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentNewPlaceBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[NewPlaceViewModel::class.java]
        arguments?.let {
            userImage=NewPlaceFragmentArgs.fromBundle(it).userImage
            userUID=NewPlaceFragmentArgs.fromBundle(it).userUID
            userName=NewPlaceFragmentArgs.fromBundle(it).userName
        }
        viewModel.findNewID()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalizeUI()
        observeLiveData()
    }

    private fun initalizeUI() {
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                placeCategory= parent.getItemAtPosition(position).toString()
                when(placeCategory){
                    "Tarihi alanlar"-> placeCategory="historical"
                    "Doğal alanlar"-> placeCategory="natural"
                    "Sahil ve Kumsallar"-> placeCategory="beach"
                    "Müzeler"-> placeCategory="museum"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                placeCity = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        binding.sendAdmin.setOnClickListener {
            val placeName=binding.placeName.text.toString()
            val placeAdress=binding.placeAdress.text.toString()
            val placePrice=binding.placePrice.text.toString()
            val placeDescription=binding.placeDescription.text.toString()
            if (placeName!="" && placeAdress !=""&& placePrice!="" && placeDescription!=""&& viewModel.uriLists.isNotEmpty())
                viewModel.addDatabase(placeName,placeAdress,placePrice,placeCategory,placeCity,placeDescription,binding.root.context)
            else
                Toast.makeText(context,R.string.fill,Toast.LENGTH_SHORT).show()
        }
        binding.placeImages.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${context?.packageName}")
                    startActivityForResult(intent, 1)
                } else {
                    loadImage()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
                    )
                } else {
                    loadImage()
                }
            }
        }
        binding.closeFragment.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.closeOneFragment.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, 1)
    }

    private fun observeLiveData() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                    binding.placeName.isEnabled=!it
                    binding.placeAdress.isEnabled=!it
                    binding.placePrice.isEnabled=!it
                    binding.placeDescription.isEnabled=!it
                    binding.spinnerCategory.isEnabled=!it
                    binding.spinnerCity.isEnabled=!it
                    binding.sendAdmin.isEnabled=!it
                    binding.closeFragment.isEnabled=!it
                    binding.closeOneFragment.isEnabled=!it

            }
        })
        viewModel.requestSuccess.observe(viewLifecycleOwner, Observer { success->
            success?.let {
                if (it=="True"){
                    Toast.makeText(binding.root.context,R.string.requestPlaceSuccess,Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }else{
                    Toast.makeText(binding.root.context,it,Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage()
            } else {
                Toast.makeText(context,getString(R.string.permissionDenied),Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.let {
                val clipData = it.clipData
                if (clipData != null) {
                    Log.e("FRAGMENT","Çoklu foto çalıştı")
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        viewModel.uploadImages(uri,i)
                    }
                } else {
                    Log.e("FRAGMENT","Tekli foto çalıştı")
                    val uri = it.data
                    viewModel.uploadImages(uri!!,0)
                }
            }
        }
    }

}