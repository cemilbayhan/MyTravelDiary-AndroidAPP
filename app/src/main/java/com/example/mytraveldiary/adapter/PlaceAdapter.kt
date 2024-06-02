package com.example.mytraveldiary.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.mytraveldiary.view.app.SearchFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.floor

/*
Adapter çalıştığında buraya placeList gelmekte. İçeriği placeList dizisidir.
İçeride ilk çalışan kısım getItemCount ve OnBindViewHolder çalışır.
Onbind içerisinden sırasıyla listenin içeriğini gezdirme işlemi başlıyor.
Her işlemde onCreateViewHolder düzeni sağlıyor.
 */
class PlaceAdapter(private val placeList: ArrayList<PlaceModel>) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {
    val firebaseAuth=FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance().reference

    inner class PlaceViewHolder(private val binding: OneplacelayoutBinding, var view: View) :
        RecyclerView.ViewHolder(binding.root) {
        val placeConstraint = binding.mainConstraint
        private val placeName = binding.placeNameText
        private val placeImage = binding.placeImg
        private val placeDescription= binding.placeDescription
        private val favBtn = binding.favCheck
        private val favLogin = binding.loginBTN

        private fun checkFav(id: String) {
            favBtn.isChecked=false
            favBtn.setButtonDrawable(R.drawable.ic_addbookmark)
            val query = firebaseDatabase.child("Users")
                .child((FirebaseAuth.getInstance().currentUser?.uid).toString())
                .child("Favorites")
                .orderByKey()
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (singleSnapshot in snapshot.children){
                        if (singleSnapshot.value==id){
                            favBtn.isChecked = true
                            favBtn.setButtonDrawable(R.drawable.ic_removebookmark)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        fun setData(placeDetail: PlaceModel) {
            getPlaceRate(placeDetail)
            checkFav(placeDetail.id)
            placeName.text = placeDetail.placeName
            placeDescription.text=setText100(placeDetail.description)
            placeImage.getImage(placeDetail.images[0], progressDrawable(view.context))
            if (firebaseAuth.currentUser!=null){
                favBtn.visibility=View.VISIBLE
                favLogin.visibility=View.GONE
                favBtn.setOnClickListener {
                    if (favBtn.isChecked){
                        favBtn.setButtonDrawable(R.drawable.ic_removebookmark)
                        firebaseDatabase
                            .child("Users")
                            .child((FirebaseAuth.getInstance().currentUser?.uid).toString())
                            .child("Favorites")
                            .child(placeDetail.id)
                            .setValue(placeDetail.id)
                    } else{
                        favBtn.setButtonDrawable(R.drawable.ic_addbookmark)
                        firebaseDatabase
                            .child("Users")
                            .child((FirebaseAuth.getInstance().currentUser?.uid).toString())
                            .child("Favorites")
                            .child(placeDetail.id)
                            .removeValue()
                    }
                }
            }
            else{
                binding.loginBTN.setOnClickListener {
                    val alert= AlertDialog.Builder(view.context)
                    alert.setTitle(R.string.needLogin)
                        .setIcon(R.drawable.ic_person_add)
                        .setNeutralButton(R.string.ok) { dialog, _ ->
                            dialog.cancel()
                        }
                        .create().show()
                }
                favBtn.visibility=View.GONE
                favLogin.visibility=View.VISIBLE
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding =
            OneplacelayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewCreate =
            LayoutInflater.from(parent.context).inflate(R.layout.oneplacelayout, parent, false)
        return PlaceViewHolder(binding, viewCreate)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val createPlaceNow =
            placeList[position]
        holder.setData(createPlaceNow)
        holder.placeConstraint.setOnClickListener {
            val mId=createPlaceNow.id.toInt()
            val placeName=createPlaceNow.placeName
            val action=SearchFragmentDirections.actionSearchFragmentToOnePlaceFragment(mId,placeName)
            Navigation.findNavController(it).navigate(action)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaceList(newPlaceList: List<PlaceModel>) {
        placeList.clear()
        placeList.addAll(newPlaceList)
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
