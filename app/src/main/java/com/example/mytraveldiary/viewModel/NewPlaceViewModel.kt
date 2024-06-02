package com.example.mytraveldiary.viewModel

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytraveldiary.model.PlaceModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.FieldPosition

class NewPlaceViewModel : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val requestSuccess=MutableLiveData<String>()


    private val list=ArrayList<String>()
    val uriLists=ArrayList<Uri>()
    private var placeID=0
    private val firebaseAuth=FirebaseAuth.getInstance().currentUser
    val firebaseDatabase=FirebaseDatabase.getInstance().reference
    lateinit var placeOne: PlaceModel

    fun addDatabase(
        placeName: String,
        placeAdress: String,
        placePrice: String,
        placeCategory: String,
        placeCity: String,
        placeDescription: String,
        context: Context
    ) {
        loading.value=true
        placeOne=PlaceModel(placeID.toString(),placeName,placeCategory,placeCity,placeDescription,placePrice,placeAdress,list)
        uploadStorage(uriLists,context,uriLists.size)
    }

    fun findNewID(){
        placeID=0
        firebaseDatabase.child("PlaceRequests").get()
            .addOnSuccessListener {
                for(nowID in it.children){
                    if (placeID.toString()!=nowID.key){
                        break
                    }
                    placeID += 1
                }
            }
    }

    fun uploadImages(path: Uri, position: Int){
        if (position==0){
            list.clear()
            uriLists.clear()
        }
        uriLists.add(path)
    }


    private fun uploadStorage(listUri:List<Uri>, context:Context, size:Int){
        for ( i in listUri.indices){
            val imageRef =
                FirebaseStorage.getInstance().reference.child("${firebaseAuth!!.uid}/images/${placeOne.placeName}/photo$i.jpg")
            val pd = ProgressDialog(context)
            pd.setTitle("Resminiz yükleniyor")
            pd.show()
            imageRef.putFile(listUri[i])
                .addOnSuccessListener {
                    pd.dismiss()
                    imageRef.downloadUrl
                        .addOnCompleteListener {
                            list.add(it.result.toString())
                            if (size==i+1){
                                finishUpload()
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
    }
    private fun finishUpload(){
        val pID=placeOne.id
        val pName=placeOne.placeName
        val pAdress=placeOne.address
        val pCategory=placeOne.category
        val pCity=placeOne.city
        val pDescrp=placeOne.description
        val pPrice=placeOne.price
        val lastPlace=PlaceModel(pID,pName,pCategory,pCity,pDescrp,pPrice,pAdress,list)
        firebaseDatabase.child("PlaceRequests").child(placeID.toString()).setValue(lastPlace)
            .addOnSuccessListener {
                requestSuccess.value="True"
            }
            .addOnFailureListener {
                requestSuccess.value=it.message
            }
    }

}