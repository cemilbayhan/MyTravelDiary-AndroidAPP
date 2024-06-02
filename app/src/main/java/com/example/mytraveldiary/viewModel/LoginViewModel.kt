package com.example.mytraveldiary.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.User
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    val loginErrorMessage = MutableLiveData<String>()
    val loginInProgress = MutableLiveData<Boolean>()
    val loginIsSuccess = MutableLiveData<Boolean>()
    val loginIsAdmin = MutableLiveData<Boolean>()

    fun signInWithFirebase(userInformation: User) {
        loginInProgress.value = true
        if (userInformation.eposta=="admin@mytraveldiary.com"&& userInformation.password=="123456"){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                userInformation.eposta,
                userInformation.password
            )
                .addOnCompleteListener {
                    loginIsAdmin.value = true
                    loginInProgress.value = false
                }
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                userInformation.eposta,
                userInformation.password
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        loginIsSuccess.value = true
                    } else {
                        loginIsSuccess.value = false
                        loginErrorMessage.value = it.exception?.message.toString()
                    }
                    loginInProgress.value = false
                }
        }


    }
}