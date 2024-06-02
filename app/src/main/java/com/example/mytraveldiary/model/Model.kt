package com.example.mytraveldiary.model

data class User(var eposta:String,
                var password:String)

data class SignUp(val nameSurname:String,
                  val email:String,
                  val password:String,
                  val imageURL: String)

data class UserProfile(val userUID:String,
                       val nameSurname:String,
                       val email:String,
                       val imageURL: String)

data class MyDate(val day:String,
                  val month:String,
                  val year:String)


data class Comments(val placeID:String,
                    val placeName: String,
                    val placeImage: String,
                    val userUID:String,
                    val userName: String,
                    val userImage:String,
                    val rate:Int,
                    val comment:String,
                    val date: MyDate)

data class NewCommentModel(val placeID:String,
                           val placeName:String,
                           val placeImage:String,
                           val userName:String,
                         val userImage:String)
