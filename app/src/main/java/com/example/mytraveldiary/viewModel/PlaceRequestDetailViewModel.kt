package com.example.mytraveldiary.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.PlaceModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PlaceRequestDetailViewModel : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val updateSuccess=MutableLiveData("null")
    val placeDetails = MutableLiveData<PlaceModel>()
    private val newPlaceID = MutableLiveData(300)

    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase = FirebaseDatabase.getInstance().reference

    fun findNewID() {
        var newID = 300
        firebaseDatabase.child("Places").get().addOnSuccessListener {
            for (snapshot in it.children) {
                if (snapshot.key.toString().toInt() == newID) {
                    newID++
                } else {
                    newPlaceID.value = newID
                    break
                }
            }
        }
    }

    fun getDatas(requestID: String) {
        loading.value = true
        firebaseDatabase.child("PlaceRequests").child(requestID)
            .get().addOnSuccessListener { snapshot ->
                snapshot?.let {

                    val placeName = snapshot.child("placeName").value.toString()
                    val placeCategory = snapshot.child("category").value.toString()
                    val placeCity = snapshot.child("city").value.toString()
                    val placePrice = snapshot.child("price").value.toString()
                    val placeAddress = snapshot.child("address").value.toString()
                    val placeDescription = snapshot.child("description").value.toString()
                    val placeImagesCount = snapshot.child("images").childrenCount
                    val placeImages = arrayListOf<String>()
                    Log.e("VMODEL","GİRDİ İÇERİ $placeName")
                    for (a in 0 until placeImagesCount) {
                        placeImages.add(
                            snapshot.child("images").child(a.toString()).value.toString()
                        )
                    }
                    val place = PlaceModel(
                        newPlaceID.value.toString(),
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
            }
    }

    fun addDatabase(requestID: String) {
        loading.value = true
        firebaseDatabase.child("Places").child(newPlaceID.value.toString()).setValue(placeDetails.value)
        removeDatabase(requestID)
    }
    fun removeDatabase(requestID: String){
        firebaseDatabase.child("PlaceRequests").child(requestID).removeValue().addOnSuccessListener {
            updateSuccess.value="True"
        }
    }


}