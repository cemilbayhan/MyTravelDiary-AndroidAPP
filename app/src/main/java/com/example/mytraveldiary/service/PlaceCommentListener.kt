package com.example.mytraveldiary.service

import com.example.mytraveldiary.model.Comments

interface PlaceCommentListener {
    fun setVisibility()
    fun refreshRecycler(newCommentList: ArrayList<Comments>)
}