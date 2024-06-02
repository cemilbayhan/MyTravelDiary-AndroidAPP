package com.example.mytraveldiary.view.app

import android.app.AlertDialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytraveldiary.R
import com.example.mytraveldiary.adapter.ImageRecyclerAdapter
import com.example.mytraveldiary.databinding.FragmentOnePlaceBinding
import com.example.mytraveldiary.service.ImageClickListener
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.viewModel.OnePlaceViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.floor


class OnePlaceFragment : Fragment(),ImageClickListener {
    private var _binding: FragmentOnePlaceBinding? = null
    private val binding get()=_binding!!
    private lateinit var viewModel: OnePlaceViewModel
    private lateinit var mID:String
    private lateinit var placeName:String

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentOnePlaceBinding.inflate(inflater,container,false)
        viewModel=ViewModelProviders.of(this)[OnePlaceViewModel::class.java]
        arguments?.let {
            mID= OnePlaceFragmentArgs.fromBundle(it).mId.toString()
            placeName=OnePlaceFragmentArgs.fromBundle(it).placeName.toString()
            viewModel.getDatas(mID)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(view)
        initializeUI(view)
        binding.imagesRecyclerView.layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initializeUI(view: View) {
        val favBtn=binding.bookmarkCheck
        viewModel.getPlaceRate(mID)
        checkFav(mID)
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.placeDescription.movementMethod = ScrollingMovementMethod()
        if (viewModel.getUser()){
            favBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    favBtn.setButtonDrawable(R.drawable.ic_removebookmark)
                    firebaseDatabase
                        .child("Users")
                        .child((FirebaseAuth.getInstance().currentUser?.uid).toString())
                        .child("Favorites")
                        .child(mID)
                        .setValue(mID)

                } else {
                    favBtn.setButtonDrawable(R.drawable.ic_addbookmark)
                    firebaseDatabase
                        .child("Users")
                        .child((FirebaseAuth.getInstance().currentUser?.uid).toString())
                        .child("Favorites")
                        .child(mID)
                        .removeValue()
                }
            }
        }else{
            favBtn.setButtonDrawable(R.drawable.bookmark)
            favBtn.setOnClickListener {
                val alert= AlertDialog.Builder(view.context)
                alert.setTitle(R.string.needLogin)
                    .setIcon(R.drawable.ic_person_add)
                    .setNeutralButton(R.string.ok) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create().show()
            }
            }

        binding.commentsText.setOnClickListener {
            goNavigate(it)
        }
        binding.starLayout.setOnClickListener {
            goNavigate(it)
        }


    }

    private fun observeLiveData(view: View) {
        viewModel.loading.observe(viewLifecycleOwner, Observer {loading ->
            loading?.let {
                    enableDisableComponents(it)
            }

        })
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(context,it,Toast.LENGTH_LONG).show()
            }

        })
        viewModel.placeDetails.observe(viewLifecycleOwner, Observer { details ->
            details?.let {
                binding.placeNameText.text=it.placeName
                binding.placeDescription.text=it.description
                binding.placePrice.text=it.price
                binding.placeAddress.text=it.address
                binding.viewPager.getImage(it.images[0], progressDrawable(requireContext()))
                binding.imagesRecyclerView.adapter=ImageRecyclerAdapter(it.images,this)
            }
        })
        viewModel.rate.observe(viewLifecycleOwner, Observer { rate->
            rate?.let {
                binding.commentsText.setCompoundDrawables(null,null,null,null)
                binding.starLayout.visibility= View.VISIBLE
                var string=getString(R.string.countComment)
                string= String.format(string,it[0],it[1])
                binding.commentsText.text=string
                setStars(it[0])
            }
        })
    }

    private fun setStars(rate: String) {
        val starList= arrayListOf(binding.starOne,binding.starTwo,binding.starThree,binding.starFour,binding.starFive)
        for (a in rate.toDouble().toInt() until 5){
            starList[a].setImageResource(R.drawable.ic_starborder)
        }
        for (a in rate.toDouble().toInt() downTo  1){
            starList[a-1].setImageResource(R.drawable.ic_starfull)
        }
        if (rate.toDouble() != floor(rate.toDouble()))
            when(floor(rate.toDouble()).toInt()){
                1->binding.starTwo.setImageResource(R.drawable.ic_starhalf)
                2->binding.starThree.setImageResource(R.drawable.ic_starhalf)
                3->binding.starFour.setImageResource(R.drawable.ic_starhalf)
                4->binding.starFive.setImageResource(R.drawable.ic_starhalf)
            }
    }

    private fun enableDisableComponents(value: Boolean) {
        if (value)
            binding.mainConstraint.visibility=View.GONE
        else
            binding.mainConstraint.visibility=View.VISIBLE

    }

    private fun checkFav(id: String) {
        //TODO SUNUMDAN SONRA İSTERSEN BURAYI VİEWMODEL İÇERİSİNE AL MİMARİYE UYGUN OLUR ŞİMDİ BOZMA
        binding.bookmarkCheck.isChecked=false
        val query = firebaseDatabase.child("Users")
            .child((FirebaseAuth.getInstance().currentUser?.uid).toString())
            .child("Favorites")
            .orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children){
                    if (singleSnapshot.value==id)
                        binding.bookmarkCheck.isChecked = true
                }


            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun goNavigate(it: View?) {
        val placeImages=viewModel.placeDetails.value!!.images
        val placeImage=placeImages[0]
        val action=OnePlaceFragmentDirections.actionOnePlaceFragmentToPlaceCommentsFragment(mID,placeName,placeImage)
        Navigation.findNavController(it!!).navigate(action)
    }

    override fun imageClickListener(position: Int) {
        setImage(position)
    }

    private fun setImage(position: Int) {
        binding.viewPager.getImage(viewModel.placeDetails.value!!.images[position], progressDrawable(requireContext()))
    }

}