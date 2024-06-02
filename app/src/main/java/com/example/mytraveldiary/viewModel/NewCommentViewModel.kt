package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.model.MyDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.TimeZone

class NewCommentViewModel: ViewModel() {
    val loading=MutableLiveData<Boolean>()
    val commentSuccess=MutableLiveData<String>()

    val rate=MutableLiveData(1)
    private var commentID= 0
    private val firebaseAuth=FirebaseAuth.getInstance().currentUser
    private val firebaseDatabase=FirebaseDatabase.getInstance().reference

    fun setNewComment(placeID:String,placeName: String,placeImage:String,userName:String,userImage:String,comment:String){
        loading.value=true
        val userUID=firebaseAuth!!.uid
        val timeZone = TimeZone.getTimeZone("Europe/Istanbul")
        val calendar = Calendar.getInstance(timeZone)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR).toString()
        val time= MyDate(currentDay,currentMonth.toString(),currentYear)
        val setComment= Comments(placeID,placeName,placeImage,userUID,userName,userImage,rate.value!!,comment,time)
        firebaseDatabase.child("Comments").child(commentID.toString()).setValue(setComment)
            .addOnSuccessListener {
                commentSuccess.value="True"
                loading.value=false
            }
            .addOnFailureListener {
                commentSuccess.value=it.message.toString()
                loading.value=false
            }
    }

    fun findNewID(){
        commentID=0
        firebaseDatabase.child("Comments").get()
            .addOnSuccessListener {
                for(nowID in it.children){
                    if (commentID.toString()!=nowID.key){
                        break
                    }
                    commentID += 1
                }
            }
    }
}