package com.example.mytraveldiary.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mytraveldiary.view.admin.PlaceRequestsFragmentDirections
import com.example.mytraveldiary.databinding.OnerequestplacelayoutBinding
import com.example.mytraveldiary.model.PlaceModel
import com.example.mytraveldiary.utils.getImage
import com.example.mytraveldiary.utils.progressDrawable
import com.example.mytraveldiary.view.admin.PlaceRequestsFragment

class AdminPlaceRequestsAdapter(
    mFragment: Fragment,
    private val requestList: ArrayList<PlaceModel>
) :
    RecyclerView.Adapter<AdminPlaceRequestsAdapter.PlaceRequestsViewHolder>() {

    inner class PlaceRequestsViewHolder(val binding: OnerequestplacelayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val placeName = binding.placeNameText
        private val placeAdress = binding.placeAdressText
        private val placePrice = binding.placePriceText
        private val placeImage = binding.placeImg
        private val placeDescription = binding.placeDescription

        fun setData(placeDetail: PlaceModel, position: Int) {
            placeName.text = placeDetail.placeName
            placeDescription.text = setText100(placeDetail.description)
            placeAdress.text = placeDetail.address
            placePrice.text = placeDetail.price
            placeImage.getImage(placeDetail.images[0], progressDrawable(binding.root.context))
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceRequestsViewHolder {
        val binding =
            OnerequestplacelayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceRequestsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: PlaceRequestsViewHolder, position: Int) {
        val createRequestNow = requestList[position]
        holder.setData(createRequestNow, position)
        holder.binding.root.setOnClickListener {
            val action = PlaceRequestsFragmentDirections.actionPlaceRequestsFragmentToPlaceRequestDetailFragment(createRequestNow.id)
            Navigation.findNavController(it).navigate(action)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaceList(newRequestList: ArrayList<PlaceModel>) {
        requestList.clear()
        requestList.addAll(newRequestList)
        notifyDataSetChanged()
    }

    fun setText100(longText: String): String {
        val maxLength = 100

        val trimmedText = if (longText.length > maxLength) {
            val endIndex = longText.indexOf('.', maxLength)
            if (endIndex != -1) {
                longText.substring(0, endIndex + 1)
            } else {
                longText.substring(0, maxLength)
            }
        } else {
            longText
        }
        return trimmedText
    }
}