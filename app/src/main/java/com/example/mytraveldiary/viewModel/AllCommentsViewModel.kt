package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.model.MyDate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllCommentsViewModel:ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val commentError = MutableLiveData<Boolean>()
    val commentList = MutableLiveData<List<Comments>>()
    val commentArrayList = ArrayList<Comments>()
    val commentCount = MutableLiveData<Int>()

    fun getCommentsFromFirebase(){
        loading.value = true
        var itemCountNow = 0
        val query = FirebaseDatabase.getInstance().reference.child("Comments")
            .orderByKey()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    if (singleSnapshot.value != null){
                        val placeID = singleSnapshot.child("placeID").value.toString()
                        val userUID = singleSnapshot.child("userUID").value.toString()
                        val placeName = singleSnapshot.child("placeName").value.toString()
                        val placeImage = singleSnapshot.child("placeImage").value.toString()
                        val userName = singleSnapshot.child("userName").value.toString()
                        val userRate = singleSnapshot.child("rate").value.toString()
                        val userImage = singleSnapshot.child("userImage").value.toString()
                        val userComment = singleSnapshot.child("comment").value.toString()
                        val day = singleSnapshot.child("date").child("day").value.toString()
                        val month = singleSnapshot.child("date").child("month").value.toString()
                        val year = singleSnapshot.child("date").child("year").value.toString()
                        val date = MyDate(day, month, year)
                        val comment = Comments(
                            placeID,
                            placeName,
                            placeImage,
                            userUID,
                            userName,
                            userImage,
                            userRate.toInt(),
                            userComment,
                            date
                        )
                        commentArrayList.add(comment)
                        itemCountNow++
                    }
                    if (itemCountNow != 0) {
                        commentList.value = commentArrayList
                    }
                    commentCount.value = itemCountNow
                    loading.value = false
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                commentError.value = true
            }
        })
    }
}