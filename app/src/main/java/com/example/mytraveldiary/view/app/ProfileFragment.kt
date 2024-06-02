package com.example.mytraveldiary.view.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.FragmentProfileBinding
import com.example.mytraveldiary.service.MainListener
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.viewModel.ProfileViewModel
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

//TODO ADMİN GİRİŞİ YAPILDIĞINDA YÖNLENDİR
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private lateinit var activityListener: MainListener
    private val PICK_IMAGE = 1
    private val MANAGE_STORAGE_PERMISSION = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        activityListener = activity as MainListener
        viewModel = ViewModelProviders.of(this)[ProfileViewModel::class.java]
        viewModel.userLogDetail()
        observeLiveData(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardListener(view.rootView)
        initializeUI(view)
    }


    private fun initializeUI(view: View) {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
        binding.rateTextView.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToUserCommentsFragment(viewModel.user.value!!.userUID)
            Navigation.findNavController(view).navigate(action)
        }

        binding.userImagePP.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${context?.packageName}")
                    startActivityForResult(intent, PICK_IMAGE)
                } else {
                    loadImage()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE
                    )
                } else {
                    loadImage()
                }
            }

        }
        binding.addLocationButton.setOnClickListener {
            val userName=viewModel.user.value!!.nameSurname
            val userUID=viewModel.user.value!!.userUID
            val userImage=viewModel.user.value!!.imageURL
            val action = ProfileFragmentDirections.actionProfileFragmentToNewPlaceFragment(userUID,userName,userImage)
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun observeLiveData(context: Context) {
        viewModel.userLoggedIn.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                if (!it) {
                    val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (it) {
                    binding.userViewConstraint.visibility = View.GONE
                    binding.profileProgressBar.visibility = View.VISIBLE
                } else {
                    binding.userViewConstraint.visibility = View.VISIBLE
                    binding.profileProgressBar.visibility = View.GONE
                }
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                print("Hata = $error")
            }
        })

        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                binding.userEmail.text = it.email
                binding.userNameSurnameText.text = it.nameSurname
                binding.userImagePP.getImage(it.imageURL, progressDrawable(context))
                //Picasso.get().load(it.imageURL).into(binding.userImagePP)
            }
        })
        viewModel.adminLogin.observe(viewLifecycleOwner, Observer { admin->
            if (admin){
                val action=ProfileFragmentDirections.actionProfileFragmentToAdminMainFragment()
                Navigation.findNavController(binding.root).navigate(action)
            }
        })
        viewModel.commentCount.observe(viewLifecycleOwner, Observer { value ->
            value?.let {
                val string = "($it)"
                binding.commentCountTextView.text = string
                if (it == 0) {
                    binding.noCommentText.visibility = View.VISIBLE
                    binding.placeImg.visibility = View.GONE
                    binding.placeNameText.visibility = View.GONE
                    binding.commentDateText.visibility = View.GONE
                    binding.rateTextView.visibility=View.GONE
                    binding.deleteIMG.visibility = View.GONE
                    binding.placeRateText.visibility = View.GONE
                    binding.starLayout.visibility = View.GONE
                    binding.userComment.visibility = View.GONE
                } else {
                    binding.noCommentText.visibility = View.GONE
                    binding.rateTextView.visibility=View.VISIBLE
                    binding.placeImg.visibility = View.VISIBLE
                    binding.placeNameText.visibility = View.VISIBLE
                    binding.commentDateText.visibility = View.VISIBLE
                    binding.deleteIMG.visibility = View.VISIBLE
                    binding.placeRateText.visibility = View.VISIBLE
                    binding.starLayout.visibility = View.VISIBLE
                    binding.userComment.visibility = View.VISIBLE
                }
            }
        })
        viewModel.comment.observe(viewLifecycleOwner, Observer { comment ->
            comment?.let {
                binding.placeNameText.text = it.placeName
                binding.placeRateText.text = it.rate.toString()
                binding.placeImg.getImage(it.placeImage, progressDrawable(context))
                setStarts(it.rate)
                val userDate = it.date
                val date = userDate.day + "/" + userDate.month + "/" + userDate.year
                binding.commentDateText.text = date
                binding.userComment.text = it.comment
                binding.deleteIMG.setOnClickListener {view->
                    val alert= AlertDialog.Builder(context)
                    alert.setTitle(R.string.areYouSureToDelete)
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton(R.string.delete){ _,_ ->
                            viewModel.deleteComment(it.userUID,it.placeID)
                        }
                        .setNeutralButton(R.string.decline) { dialog, _ ->
                            dialog.cancel()
                        }
                        .create().show()
                }
            }
        })
        viewModel.selectingImage.observe(viewLifecycleOwner, Observer {uri->
            uri?.let {
                viewModel.uploadImage(it,context)
            }
        })
        viewModel.refreshPhoto.observe(viewLifecycleOwner, Observer { refresh->
            refresh?.let {
                if (it){
                    viewModel.userLogDetail()
                    viewModel.refreshPhoto.value=false
                }

            }
        })
    }


    private fun setStarts(rate: Int) {
        val starList = arrayListOf(
            binding.starOne,
            binding.starTwo,
            binding.starThree,
            binding.starFour,
            binding.starFive
        )
        for (a in rate until 5) {
            starList[a].setImageResource(R.drawable.ic_starborder)
        }
        for (a in rate downTo 1) {
            starList[a - 1].setImageResource(R.drawable.ic_starfull)
        }

    }
    private fun keyboardListener(rootView: View?) {
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView?.height

            val keypadHeight = screenHeight?.minus(r.bottom)

            if (keypadHeight != null) {
                if (keypadHeight > screenHeight * 0.15)
                    activityListener.showOrHide(false)
                else
                    activityListener.showOrHide(true)
            }
        }
    }


    // IMAGE UPLOAD FUNCTIONS
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            try {
                selectedImageUri?.let {
                    viewModel.selectingImage.value=it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == MANAGE_STORAGE_PERMISSION) {
            if (Environment.isExternalStorageManager()) {
                loadImage()
            } else {
                Toast.makeText(context,getString(R.string.permissionDenied),Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PICK_IMAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage()
            } else {
                Toast.makeText(context,getString(R.string.permissionDenied),Toast.LENGTH_SHORT).show()
            }
        }
    }
}