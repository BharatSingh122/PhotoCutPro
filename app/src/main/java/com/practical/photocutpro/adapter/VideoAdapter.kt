package com.practical.photocutpro.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practical.photocutpro.R
import com.practical.photocutpro.databinding.VideoImageBinding
import java.io.File

class VideoAdapter(private val mContext: Activity ,  val listener: OnItemClickListener) : RecyclerView.Adapter<VideoAdapter.ImageViewHolder>() {
    private var videoList: List<File> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)

        val binding = VideoImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageFile = videoList[position]

        holder.binding.tvName.text = videoList[position].name

        Glide.with(mContext)
            .load(imageFile)
            .into(holder.binding.imageView)


        holder.binding.playButton.setOnClickListener {

            listener.onItemClick(position , videoList)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun setImages(images: List<File>) {
        this.videoList = images
        notifyDataSetChanged()
    }

    class ImageViewHolder(val binding: VideoImageBinding) : RecyclerView.ViewHolder(binding.root) {
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int , videoList : List<File>)
    }

}
