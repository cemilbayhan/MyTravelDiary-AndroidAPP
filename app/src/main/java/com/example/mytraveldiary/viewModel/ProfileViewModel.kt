package com.example.mytraveldiary.viewModel

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.model.MyDate
import com.example.mytraveldiary.model.UserProfile
import com.example.mytraveldiary.view.app.UserCommentsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import kotlin.random.Random

class ProfileViewModel:ViewModel() {
    val loading= MutableLiveData<Boolean>()
    val error= MutableLiveData<String>()
    val adminLogin=MutableLiveData(false)
    val userLoggedIn = MutableLiveData(true)
    val refreshPhoto= MutableLiveData<Boolean>()
    val comment = MutableLiveData<Comments>()
    val user = MutableLiveData<UserProfile>()
    val commentCount = MutableLiveData<Int>()
    private val list = mutableListOf<DataSnapshot>()
    val selectingImage=MutableLiveData<Uri>()



    private val firebaseAuth=FirebaseAuth.getInstance()
    private val firebaseDatabase=FirebaseDatabase.getInstance().reference

    fun userLogDetail(){
        loading.value=true
        if(firebaseAuth.currentUser!=null){
            checkAdminLogin()
            getViewComponents()
        }else{
            loading.value=false
            userLoggedIn.value = false
        }

    }

    private fun checkAdminLogin() {
        val email=firebaseAuth.currentUser!!.email
        if (email=="admin@bulkesfet.com"){
            adminLogin.value=true
        }
    }

    fun logout(){
        firebaseAuth.signOut()
        userLogDetail()
    }

    private fun getViewComponents(){
        getUser()
        getCount()
    }

    private fun getCount() {
        var countDown=0
        firebaseDatabase.child("Comments").get().addOnSuccessListener {
                for(snapshot in it.children){
                    if (snapshot.value!=null){
                        if(snapshot.child("userUID").value==firebaseAuth.currentUser!!.uid){
                            list.add(snapshot)
                            countDown++
                        }
                    }
                }
                if (countDown!=0){
                    val randomComment=list[Random.nextInt(countDown)]
                    val placeID=randomComment.child("placeID").value.toString()
                    val placeName=randomComment.child("placeName").value.toString()
                    val placeImage=randomComment.child("placeImage").value.toString()
                    val placeRate=randomComment.child("rate").value.toString().toInt()
                    val placeComment=randomComment.child("comment").value.toString()
                    val userUID=randomComment.child("userUID").value.toString()
                    val userName=randomComment.child("userName").value.toString()
                    val userImage=randomComment.child("userImage").value.toString()
                    val placeDay=randomComment.child("date").child("day").value.toString()
                    val placeMonth=randomComment.child("date").child("month").value.toString()
                    val placeYear=randomComment.child("date").child("year").value.toString()
                    val commentDate=MyDate(placeDay,placeMonth,placeYear)
                    val commentOne= Comments(placeID,placeName,placeImage,userUID,userName,userImage,placeRate,placeComment,commentDate)
                    comment.value=commentOne
                }
                commentCount.value=countDown
                loading.value=false
            }
    }

    private fun getUser(){
        firebaseDatabase.child("Users").child(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.value!=null){
                    val userNameSurname=it.child("nameSurname").value.toString()
                    val userEmail=it.child("email").value.toString()
                    val imageURL=it.child("imageURL").value.toString()
                    user.value=UserProfile(firebaseAuth.currentUser!!.uid,userNameSurname,userEmail,imageURL)
                }
            }

    }

    fun uploadImage(path: Uri,context: Context){
        val imageRef =
            FirebaseStorage.getInstance().reference.child("${firebaseAuth.uid}/images/profilepicture.jpg")
        val pd = ProgressDialog(context)
        pd.setTitle("Resminiz hazırlanıyor")
        pd.show()
        imageRef.putFile(path)
            .addOnSuccessListener {
                pd.dismiss()
                imageRef.downloadUrl
                    .addOnCompleteListener {
                        val imageDatabaseURL = it.result.toString()
                        firebaseDatabase.child("Users")
                            .child(firebaseAuth.uid.toString())
                            .child("imageURL")
                            .setValue(imageDatabaseURL)
                            .addOnSuccessListener {
                                refreshPhoto.value=true
                            }

                    }
            }
            .addOnFailureListener {
                pd.dismiss()
            }
            .addOnProgressListener {
                val progress = (100 * it.bytesTransferred) / it.totalByteCount
                pd.setMessage("Uploaded: ${progress.toInt()}%")
            }
    }

    fun deleteComment(userUID:String,placeID:String) {
        firebaseDatabase.child("Comments").get()
            .addOnSuccessListener {
                var commentID = "null"
                for (snapshot in it.children) {
                    if (snapshot.child("userUID").value == userUID
                        && snapshot.child("placeID").value == placeID
                    ) {
                        commentID = snapshot.key.toString()
                        break
                    }
                }
                if (commentID != "null")
                    FirebaseDatabase.getInstance().reference.child("Comments").child(commentID)
                        .removeValue()
                getViewComponents()
            }
    }

}