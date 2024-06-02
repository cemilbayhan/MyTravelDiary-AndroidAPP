package com.example.mytraveldiary.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mytraveldiary.R
import com.example.mytraveldiary.databinding.AdmincommentlayoutBinding
import com.example.mytraveldiary.model.Comments
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.view.admin.AllCommentsFragment
import com.google.firebase.database.FirebaseDatabase


class AllCommentsAdapter(mFragment: Fragment,
                         private val commentList: ArrayList<Comments>
) : RecyclerView.Adapter<AllCommentsAdapter.CommentsViewHolder>() {
    private val myFragment=mFragment

    inner class CommentsViewHolder(val binding: AdmincommentlayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val placeName= binding.placeNameText
        private val userName= binding.userNameText
        private val placeImage= binding.placeImg
        private val userRate=binding.placeRateText
        private val comment= binding.placeDescription
        private val date= binding.commentDateText

            fun setData(commentDetail:Comments,position: Int){
                placeName.text=commentDetail.placeName
                userName.text=commentDetail.userName
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


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllCommentsAdapter.CommentsViewHolder {
        val binding= AdmincommentlayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllCommentsAdapter.CommentsViewHolder, position: Int) {
        val createCommentNow = commentList[position]
        holder.setData(createCommentNow,position)
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
                            (myFragment as AllCommentsFragment).recyclerOlustur(commentList)
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