package com.example.mytraveldiary.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.OnecommentlayoutBinding
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.service.PlaceCommentListener
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.view.app.PlaceCommentsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PlaceCommentAdapter(mFragment:Fragment, private val commentList: ArrayList<Comments>,
                          private val commentListener: PlaceCommentListener) :
    RecyclerView.Adapter<PlaceCommentAdapter.PlaceCommentViewHolder>() {
    private val myFragment=mFragment
    private var flag=0
    inner class PlaceCommentViewHolder(val binding: OnecommentlayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val userName = binding.userNameText
        private val userImage = binding.Img
        private val userRate = binding.userRateText
        private val userDate = binding.commentDateText
        private val userComment = binding.userComment
        private val firebaseAuth=FirebaseAuth.getInstance().currentUser

        fun setData(commentDetail: Comments){
            checkUser(commentDetail)
            userName.text=commentDetail.userName
            userImage.getImage(commentDetail.userImage, progressDrawable(binding.root.context))
            userRate.text=commentDetail.rate.toString()
            if (commentDetail.rate==5)
                userRate.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_starfull, 0, 0, 0)
            else
                userRate.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_starhalf, 0, 0, 0)
            userComment.text=commentDetail.comment
            val date=commentDetail.date.day+"/"+commentDetail.date.month+"/"+commentDetail.date.year
            userDate.text=date
        }
        private fun checkUser(comment:Comments) {
            if (firebaseAuth!=null) {
                if (firebaseAuth.uid==comment.userUID){
                    flag=1
                    binding.userImagesGroup.visibility=View.VISIBLE
                } else {
                    binding.userImagesGroup.visibility=View.GONE
                }
            }
            else{
                binding.userImagesGroup.visibility=View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceCommentViewHolder {
        val binding =
            OnecommentlayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceCommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: PlaceCommentViewHolder, position: Int) {
        val createCommentNow = commentList[position]
        holder.setData(createCommentNow)
        holder.binding.deleteIMG.setOnClickListener {
            val alert= AlertDialog.Builder(holder.binding.root.context)
            alert.setTitle(R.string.areYouSureToDelete)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.delete){ _,_ ->
                    FirebaseDatabase.getInstance().reference.child("Comments").get()
                        .addOnSuccessListener {
                            var commentID="null"
                            for (snapshot in it.children){
                                val userUID=commentList[position].userUID
                                val placeID=commentList[position].placeID
                                if (snapshot.child("userUID").value==userUID
                                    && snapshot.child("placeID").value==placeID){
                                    commentID=snapshot.key.toString()
                                    break
                                }
                            }
                            if (commentID!="null")
                                FirebaseDatabase.getInstance().reference.child("Comments").child(commentID).removeValue()
                            commentList.removeAt(position)
                            (myFragment as PlaceCommentsFragment).recyclerOlustur(commentList)
                        }
                }
                .setNeutralButton(R.string.decline) { dialog, _ ->
                    dialog.cancel()
                }
                .create().show()
        }
        if (commentList.size==position+1 && flag==0){
            commentListener.setVisibility()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaceList(newCommentList: List<Comments>) {
        commentList.clear()
        notifyDataSetChanged()
        commentList.addAll(newCommentList)
    }
}

