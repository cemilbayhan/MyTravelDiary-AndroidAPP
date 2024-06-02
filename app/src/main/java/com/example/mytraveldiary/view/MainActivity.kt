package com.example.mytraveldiary.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.mytraveldiary.R
import com.example.mytraveldiary.service.MainListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity(), MainListener {
    private lateinit var navController: NavController
    private lateinit var bottomMenu: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController= navHostFragment.navController
        bottomMenu= findViewById(R.id.bottomNavigationMenu)
        bottomMenu.itemIconTintList = null
        setupWithNavController(bottomMenu,navController)

    }

    override fun showOrHide(value: Boolean) {
        if(value) bottomMenu.visibility= View.VISIBLE
        else bottomMenu.visibility=View.GONE
    }

    override fun getFilesDirBenim(): File {
        return File(filesDir,"croppedImage.jpg")
    }
}