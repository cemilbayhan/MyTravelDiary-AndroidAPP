package com.example.mytraveldiary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.OneplacelayoutBinding
import com.example.mytraveldiary.model.PlaceModel
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.view.app.FavoritesFragment
import com.example.mytraveldiary.view.app.FavoritesFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.floor

class FavAdapter(mFragment: Fragment, private val favList: ArrayList<PlaceModel>) :
    RecyclerView.Adapter<FavAdapter.FavViewHolder>() {
    val myFragment=mFragment
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    inner class FavViewHolder(private val binding: OneplacelayoutBinding, var view: View) : RecyclerView.ViewHolder(binding.root) {

        val placeConstraint = binding.mainConstraint
        private val placeName = binding.placeNameText
        private val placeImage = binding.placeImg
        private val placeDescription= binding.placeDescription
        private val favBtn = binding.favCheck

        fun setData(placeDetail: PlaceModel, position: Int) {
            getPlaceRate(placeDetail)
            placeName.text = placeDetail.placeName
            placeDescription.text=setText100(placeDetail.description)
            placeImage.getImage(placeDetail.images[0], progressDrawable(view.context))

            favBtn.isChecked=true
            favBtn.setButtonDrawable(R.drawable.ic_removebookmark)
            favBtn.setOnClickListener {
                favList.removeAt(position)
                firebaseDatabase
                    .child("Users")
                    .child((FirebaseAuth.getInstance().currentUser?.uid).toString())
                    .child("Favorites")
                    .child(placeDetail.id)
                    .removeValue()
                (myFragment as FavoritesFragment).recyclerOlustur(favList)

            }
        }

        private fun getPlaceRate(placeDetail: PlaceModel) {
            val placerate= arrayListOf<Int>()
            firebaseDatabase.child("Comments").get().addOnSuccessListener {
                for (snapshot in it.children){
                    if (snapshot.child("placeID").value.toString()==placeDetail.id){
                        placerate.add(snapshot.child("rate").value.toString().toInt())
                    }
                }
                if(placerate.isNotEmpty()){
                    binding.placeRateText.setCompoundDrawables(null,null,null,null)
                    binding.starLayout.visibility=View.VISIBLE
                    var string=binding.root.context.getString(R.string.countComment)
                    string= String.format(string,placerate.average().toString(),placerate.size.toString())
                    binding.placeRateText.text=string
                    setStars(placerate.average())
                    placerate.clear()
                }else{
                    binding.placeRateText.setText(R.string.noCommentFound)
                    binding.starLayout.visibility=View.GONE
                    binding.placeRateText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_starborder, 0, 0, 0)
                }
            }

        }

        private fun setStars(average: Double) {
            val starList= arrayListOf(binding.starOne,binding.starTwo,binding.starThree,binding.starFour,binding.starFive)
            for (a in average.toInt() until 5){
                starList[a].setImageResource(R.drawable.ic_starborder)
            }
            for (a in average.toInt() downTo  1){
                starList[a-1].setImageResource(R.drawable.ic_starfull)
            }
            if (average != floor(average))
                when(floor(average).toInt()){
                    1->binding.starTwo.setImageResource(R.drawable.ic_starhalf)
                    2->binding.starThree.setImageResource(R.drawable.ic_starhalf)
                    3->binding.starFour.setImageResource(R.drawable.ic_starhalf)
                    4->binding.starFive.setImageResource(R.drawable.ic_starhalf)
                }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding =
            OneplacelayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewCreate =
            LayoutInflater.from(parent.context).inflate(R.layout.oneplacelayout, parent, false)
        return FavViewHolder(binding, viewCreate)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val createPlaceNow = favList[position]
        holder.setData(createPlaceNow,position)
        holder.placeConstraint.setOnClickListener {
            val mId=createPlaceNow.id.toInt()
            val action= FavoritesFragmentDirections.actionFavoritesFragmentToOnePlaceFragment(mId)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFavList(newFavList: ArrayList<PlaceModel>){
        favList.clear()
        favList.addAll(newFavList)
        notifyDataSetChanged()
    }

    fun setText100(longText: String): String {
        val maxLength = 100

        val trimmedText = if (longText.length > maxLength) {
            val endIndex = longText.indexOf('.', maxLength)
            if (endIndex != -1) {
                longText.substring(0, endIndex + 1)
            } else {
                longText.substring(0, maxLength)
            }
        } else {
            longText
        }
        return trimmedText
    }

}