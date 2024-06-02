package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.PlaceModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FavoritesViewModel: ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData(false)

    val itemCount=MutableLiveData<Int>()
    val places=MutableLiveData<ArrayList<PlaceModel>>()

    private val firebaseAuth=FirebaseAuth.getInstance()
    private val firebaseDatabase=FirebaseDatabase.getInstance().reference

    fun getDatas(){
        loading.value=true
        if(firebaseAuth.currentUser!=null){
            itemCount.value=0
            places.value= arrayListOf()
            val placeIDS=arrayListOf<String>()
            val favList=ArrayList<PlaceModel>()
            var itemCountNow=0
            val query = firebaseDatabase.child("Users")
                .child((firebaseAuth.currentUser?.uid).toString())
                .child("Favorites")
                .orderByKey()
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (singleSnapshot in snapshot.children) {
                        if (singleSnapshot.value != null) {
                            val placeID = singleSnapshot.value.toString()
                            placeIDS.add(placeID)
                            itemCountNow++
                        }
                    }
                    if (itemCountNow==0)
                        loading.value=false

                    for (a in placeIDS){
                        firebaseDatabase.child("Places")
                            .child(a)
                            .orderByKey()
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val placeName = snapshot.child("placeName").value.toString()
                                    val placeCategory = snapshot.child("category").value.toString()
                                    val placeDescription = snapshot.child("description").value.toString()
                                    val placeAdress = snapshot.child("address").value.toString()
                                    val placeCity = snapshot.child("city").value.toString()
                                    val placePrice = snapshot.child("price").value.toString()
                                    val placeImagesCount = snapshot.child("images").childrenCount
                                    val placeImages= arrayListOf<String>()
                                    for (b in 0 until placeImagesCount)
                                        placeImages.add(snapshot.child("images").child(b.toString()).value.toString())
                                    val place = PlaceModel(a,placeName,placeCategory,placeCity,placeDescription,placePrice,placeAdress,placeImages)
                                    favList.add(place)
                                    if (placeIDS[itemCountNow-1]==a){
                                        itemCount.value=itemCountNow
                                        places.value=favList
                                        loading.value=false
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    println("Hata Oluştu Database hatası")
                                }
                            })


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Hata Oluştu Database hatası")
                }
            })

        }else{
            error.value=true
            loading.value=false
        }

    }
}