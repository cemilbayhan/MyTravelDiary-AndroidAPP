package com.example.mytraveldiary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mytraveldiary.databinding.ImagesRecyclerviewitemBinding
import com.example.mytraveldiary.service.ImageClickListener
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable


class ImageRecyclerAdapter(private val imageList: List<String>, private val imageClickListener:ImageClickListener) :
    RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ImagesRecyclerviewitemBinding) :
    RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ImagesRecyclerviewitemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.image.getImage(imageList[position], progressDrawable(holder.binding.root.context) )
        holder.binding.root.setOnClickListener {
            imageClickListener.imageClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}