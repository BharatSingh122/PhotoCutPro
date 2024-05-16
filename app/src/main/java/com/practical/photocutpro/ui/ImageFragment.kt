package com.practical.photocutpro.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.practical.photocutpro.MainActivity
import com.practical.photocutpro.R
import com.practical.photocutpro.adapter.ImageAdapter
import com.practical.photocutpro.databinding.FragmentImageBinding
import com.practical.photocutpro.viewmodel.ImageViewModel

class ImageFragment : Fragment() {

    lateinit var binding: FragmentImageBinding
//    private lateinit var viewModel: ImageViewModel
    lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(layoutInflater, container, false)
        val view = binding.root



        adapter = ImageAdapter(requireActivity(), object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(path: String) {

                val intent = Intent(requireActivity(), ImageShowActivity::class.java)
                intent.putExtra("path", path)
                startActivity(intent)

            }

        })

        binding.rvImageList.layoutManager = GridLayoutManager(activity, 2)
        binding.rvImageList.adapter = adapter

        MainActivity.viewModel.imagesLiveData.observe(requireActivity(), Observer { imageFiles ->

            if (imageFiles.isNotEmpty()) {
                binding.tvNoData.visibility = View.GONE
            }


            adapter.setImages(imageFiles)
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        if (MainActivity.viewModel!=null){
            MainActivity.viewModel.loadImages()
        }
    }

}