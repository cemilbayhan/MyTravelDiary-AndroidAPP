package com.example.mytraveldiary.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.OneplacelayoutBinding
import com.example.mytraveldiary.databinding.OneusercommentlayoutBinding
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.view.app.FavoritesFragment
import com.example.mytraveldiary.view.app.UserCommentsFragment
import com.google.firebase.database.FirebaseDatabase

class UserCommentAdapter (mFragment:Fragment,private val commentList: ArrayList<Comments>) :
    RecyclerView.Adapter<UserCommentAdapter.UserCommentViewHolder>(){
    private val myFragment=mFragment
    inner class UserCommentViewHolder(val binding: OneusercommentlayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val placeName= binding.placeNameText
        private val placeImage= binding.placeImg
        private val userRate=binding.placeRateText
        private val comment= binding.placeDescription
        private val date= binding.commentDateText
        fun setData(commentDetail:Comments){
            placeName.text=commentDetail.placeName
            placeImage.getImage(commentDetail.placeImage, progressDrawable(binding.root.context))
            userRate.text=commentDetail.rate.toString()
            setStars(commentDetail.rate)
            comment.text=commentDetail.comment
            val string="${commentDetail.date.day}/${commentDetail.date.month}/${commentDetail.date.year}"
            date.text=string
        }

        private fun setStars(rate: Int) {
            val starList= arrayListOf(binding.starOne,binding.starTwo,binding.starThree,binding.starFour,binding.starFive)
            for (a in rate until 5){
                starList[a].setImageResource(R.drawable.ic_starborder)
            }
            for (a in rate downTo  1){
                starList[a-1].setImageResource(R.drawable.ic_starfull)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCommentAdapter.UserCommentViewHolder {
        val binding = OneusercommentlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserCommentAdapter.UserCommentViewHolder, position: Int) {
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
                            (myFragment as UserCommentsFragment).recyclerOlustur(commentList)
                        }
                }
                .setNeutralButton(R.string.decline) { dialog, _ ->
                    dialog.cancel()
                }
                .create().show()
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCommentList(newCommentList: List<Comments>) {
        commentList.clear()
        notifyDataSetChanged()
        commentList.addAll(newCommentList)
    }

}