package com.example.mytraveldiary.service

import java.io.File

interface MainListener {
    fun showOrHide(value:Boolean)
    fun getFilesDirBenim(): File
}