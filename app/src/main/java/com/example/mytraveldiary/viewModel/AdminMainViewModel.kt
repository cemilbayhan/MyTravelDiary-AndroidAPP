package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AdminMainViewModel:ViewModel() {
    val logout=MutableLiveData(false)

    private val firebaseAuth=FirebaseAuth.getInstance()

    fun logout(){
        firebaseAuth.signOut()
        logout.value = firebaseAuth.currentUser == null
    }
}