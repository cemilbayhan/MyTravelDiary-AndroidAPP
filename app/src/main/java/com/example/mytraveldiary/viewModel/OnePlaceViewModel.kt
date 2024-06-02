package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.PlaceModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OnePlaceViewModel : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val placeDetails = MutableLiveData<PlaceModel>()
    val error = MutableLiveData<String>()
    val rate= MutableLiveData<ArrayList<String>>()

    val firebaseDatabase=FirebaseDatabase.getInstance().reference
    fun getDatas(mID: String) {
        loading.value = true
        val query = firebaseDatabase.child("Places").child(mID)
            .orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val placeName = snapshot.child("placeName").value.toString()
                val placeCategory = snapshot.child("category").value.toString()
                val placeCity = snapshot.child("city").value.toString()
                val placePrice = snapshot.child("price").value.toString()
                val placeAddress = snapshot.child("address").value.toString()
                val placeDescription = snapshot.child("description").value.toString()
                val placeImagesCount = snapshot.child("images").childrenCount
                val placeImages = arrayListOf<String>()
                for (a in 0 until placeImagesCount) {
                    placeImages.add(
                        snapshot.child("images").child(a.toString()).value.toString()
                    )
                }
                val place = PlaceModel(
                    mID,
                    placeName,
                    placeCategory,
                    placeCity,
                    placeDescription,
                    placePrice,
                    placeAddress,
                    placeImages
                )
                placeDetails.value = place
                loading.value = false
            }


            override fun onCancelled(databaseError: DatabaseError) {
                error.value=databaseError.message
            }
        })


    }

    fun getPlaceRate(placeID: String){
        val placerate= arrayListOf<Int>()
        firebaseDatabase.child("Comments").get().addOnSuccessListener {
            for (snapshot in it.children){
                if (snapshot.child("placeID").value.toString()==placeID){
                    placerate.add(snapshot.child("rate").value.toString().toInt())
                }
            }
            if(placerate.isNotEmpty())
                rate.value= arrayListOf(placerate.average().toString(),placerate.size.toString())

        }
    }

    fun getUser():Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}