package com.practical.photocutpro.adapter

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practical.photocutpro.R
import com.practical.photocutpro.databinding.ItemImageBinding
import java.io.File

class ImageAdapter(private val mContext: Activity , val clickListener: OnItemClickListener) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private var images: List<File> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)

        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageFile = images[position]

        val displayMetrics = DisplayMetrics()

        mContext.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth =
            displayMetrics.widthPixels - 60


        val mParams = holder.binding.imageView.layoutParams
        mParams.width = (screenWidth / 2f).toInt()
        mParams.height = (screenWidth / 1.4f).toInt()

        holder.binding.imageView.layoutParams = mParams

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(images[position].absolutePath)
        }

        Glide.with(mContext)
            .load(imageFile)
            .into(holder.binding.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setImages(images: List<File>) {
        this.images = images
        notifyDataSetChanged()
    }

    class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface OnItemClickListener {
        fun onItemClick(path : String)
    }
}
