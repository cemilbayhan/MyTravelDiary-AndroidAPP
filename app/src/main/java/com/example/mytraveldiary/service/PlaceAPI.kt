package com.example.mytraveldiary.service

import com.example.mytraveldiary.model.PlaceModel
import retrofit2.Call
import retrofit2.http.GET

interface PlaceAPI {
    @GET("mytraveldiary/place2")
    fun getPlaceDetails(): Call<List<PlaceModel>>
}

