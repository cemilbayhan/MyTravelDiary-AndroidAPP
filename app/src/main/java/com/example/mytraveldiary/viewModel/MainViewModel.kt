package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.model.MyDate
import com.example.mytraveldiary.model.PlaceModel
import com.example.mytraveldiary.service.PlaceAPI
import com.example.mytraveldiary.utils.BASE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {
    val error = MutableLiveData<String>()
    // TODO API VERİ ÇEKME İŞİ BİTTİYSE BU VİEWMODELİ SİLEBİLİRSİN


    // API DEN VERİTABANI VERİ KOPYALAMASI
    fun checkDatabase() {

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaceAPI::class.java)

        val apiCall = api.getPlaceDetails()
        apiCall.enqueue(object : Callback<List<PlaceModel>> {
            override fun onResponse(
                call: Call<List<PlaceModel>>,
                response: Response<List<PlaceModel>>
            ) {

                val sizeResponse = response.body()?.size!!
                for (a in 0 until sizeResponse) {
                    val mID = response.body()?.get(a)?.id
                    val placeName = response.body()?.get(a)?.placeName
                    val placeDescription = response.body()?.get(a)?.description
                    val placeCategory = response.body()?.get(a)?.category
                    val placeCity = response.body()?.get(a)?.city
                    val placeAdress = response.body()?.get(a)?.address
                    val placePrice = response.body()?.get(a)?.price
                    val placeImages = response.body()?.get(a)?.images

                    if (mID != "null"
                        && placeName != ""
                        && placeDescription != ""
                        && placeCategory != ""
                        && placeCity != ""
                        && placeAdress != ""
                        && placePrice != ""
                    ) {
                        val place = PlaceModel(
                            mID!!,
                            placeName!!,
                            placeCategory!!,
                            placeCity!!,
                            placeDescription!!,
                            placePrice!!,
                            placeAdress!!,
                            placeImages!!
                        )
                        checkPlaceInFirebase(place)
                    }

                }
            }
            override fun onFailure(call: Call<List<PlaceModel>>, t: Throwable) {
                println("Hata: ${t.message}")
            }
        })

    }

    private fun checkPlaceInFirebase(place: PlaceModel) {

        FirebaseDatabase.getInstance().reference.child("Places").child(place.id).child("id").get()
            .addOnSuccessListener {
                if (it.value.toString() != place.id) {
                    addPlaceToFirebase(place)
                }
            }.addOnFailureListener { println("Hata, ${it.message}") }
    }

    private fun addPlaceToFirebase(place: PlaceModel) {
        FirebaseDatabase.getInstance().reference.child("Places").child(place.id).setValue(place)
    }
}