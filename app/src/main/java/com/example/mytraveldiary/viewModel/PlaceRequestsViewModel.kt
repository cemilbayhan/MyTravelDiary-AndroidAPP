package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.PlaceModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlaceRequestsViewModel : ViewModel() {
    val loading = MutableLiveData<Boolean>()

    val itemCount = MutableLiveData<Int>()
    val requests = MutableLiveData<ArrayList<PlaceModel>>()


    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    fun getDatas() {
        loading.value = true
        itemCount.value = 0
        requests.value = arrayListOf()
        val requestList = arrayListOf<PlaceModel>()
        var itemCountNow = 0
        val query = firebaseDatabase.child("PlaceRequests")
            .orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    if (singleSnapshot.value != null) {
                        val placeID = singleSnapshot.child("id").value.toString()
                        val placeName = singleSnapshot.child("placeName").value.toString()
                        val placeAdress = singleSnapshot.child("address").value.toString()
                        val placePrice = singleSnapshot.child("price").value.toString()
                        val placeCity = singleSnapshot.child("city").value.toString()
                        val placeCategory = singleSnapshot.child("category").value.toString()
                        val placeDescription = singleSnapshot.child("description").value.toString()
                        val placeImagesCount = singleSnapshot.child("images").childrenCount
                        val placeImages = arrayListOf<String>()
                        for (a in 0 until placeImagesCount) {
                            placeImages.add(singleSnapshot.child("images").child(a.toString()).value.toString())
                        }
                        val onePlace=PlaceModel(placeID,placeName,placeCategory,placeCity,placeDescription,placePrice,placeAdress,placeImages)
                        requestList.add(onePlace)
                        itemCountNow++
                    }
                }
                loading.value=false
                itemCount.value=itemCountNow
                requests.value=requestList
            }
            override fun onCancelled(error: DatabaseError) {
                println("Hata Oluştu Database hatası")
            }
        })

    }
}
