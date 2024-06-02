package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.PlaceModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchViewModel : ViewModel() {
    var placeList = MutableLiveData<List<PlaceModel>>()
    var placeError = MutableLiveData<Boolean>()
    var placeLoading = MutableLiveData<Boolean>()

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    val placeArrayList = ArrayList<PlaceModel>()


    fun getDataFromCity(queryPlaceCity:String){
        placeArrayList.clear()
        placeError.value = false
        placeLoading.value = true
        val query = firebaseDatabase.child("Places").orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    if (singleSnapshot.value != null) {
                        val placeID = singleSnapshot.child("id").value.toString()
                        val placeName = singleSnapshot.child("placeName").value.toString()
                        val placeCategory = singleSnapshot.child("category").value.toString()
                        val placeCity = singleSnapshot.child("city").value.toString()
                        val placeDescription = singleSnapshot.child("description").value.toString()
                        val placeAdress = singleSnapshot.child("address").value.toString()
                        val placePrice = singleSnapshot.child("price").value.toString()
                        val placeImagesCount = singleSnapshot.child("images").childrenCount
                        val placeImages= arrayListOf<String>()
                        for (a in 0 until placeImagesCount){
                            placeImages.add(singleSnapshot.child("images").child(a.toString()).value.toString())
                        }
                        val places = PlaceModel(placeID,placeName,placeCategory,placeCity,placeDescription,placePrice,placeAdress,placeImages)
                        if(placeCity==queryPlaceCity ||queryPlaceCity=="Hepsi")
                            placeArrayList.add(places)
                    }
                }
                placeList.value=placeArrayList
                placeLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                placeError.value=true
            }
        })
    }
    fun getDataFromCategory(queryPlaceName:String){
        placeArrayList.clear()
        placeError.value = false
        placeLoading.value = true
        val query = firebaseDatabase.child("Places").orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    if (singleSnapshot.value != null) {
                        val placeID = singleSnapshot.child("id").value.toString()
                        val placeName = singleSnapshot.child("placeName").value.toString()
                        val placeCategory = singleSnapshot.child("category").value.toString()
                        val placeCity = singleSnapshot.child("city").value.toString()
                        val placeDescription = singleSnapshot.child("description").value.toString()
                        val placeAdress = singleSnapshot.child("address").value.toString()
                        val placePrice = singleSnapshot.child("price").value.toString()
                        val placeImagesCount = singleSnapshot.child("images").childrenCount
                        val placeImages= arrayListOf<String>()
                        for (a in 0 until placeImagesCount){
                            placeImages.add(singleSnapshot.child("images").child(a.toString()).value.toString())
                        }
                        val places = PlaceModel(placeID,placeName,placeCategory,placeCity,placeDescription,placePrice,placeAdress,placeImages)
                        when (queryPlaceName){
                            "Tarihi Alanlar" -> {
                                if (places.category=="historical")
                                    placeArrayList.add(places)
                            }
                            "Sahil ve Kumsallar" -> {
                                if (places.category=="beach")
                                    placeArrayList.add(places)
                            }
                            "Doğal Alanlar" -> {
                                if (places.category=="natural")
                                    placeArrayList.add(places)
                            }
                            "Müzeler" -> {
                                if (places.category=="museum")
                                    placeArrayList.add(places)
                            }
                            else -> placeArrayList.add(places)

                        }
                    }
                }
                placeList.value=placeArrayList
                placeLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                placeError.value=true
            }
        })
    }

}