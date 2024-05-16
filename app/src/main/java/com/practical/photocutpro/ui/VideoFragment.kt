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
import androidx.recyclerview.widget.LinearLayoutManager
import com.practical.photocutpro.MainActivity
import com.practical.photocutpro.adapter.VideoAdapter
import com.practical.photocutpro.databinding.FragmentVideoBinding
import com.practical.photocutpro.viewmodel.ImageViewModel
import java.io.File

class VideoFragment : Fragment() {

    lateinit var binding : FragmentVideoBinding
    private lateinit var viewModel: ImageViewModel
    lateinit var adapter : VideoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentVideoBinding.inflate(layoutInflater , container , false)
        val view = binding.root


        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[ImageViewModel::class.java]

        adapter = VideoAdapter(requireActivity() , object : VideoAdapter.OnItemClickListener{
            override fun onItemClick(position: Int , videoList : List<File>) {

                val videoPaths = ArrayList(videoList.map { it.absolutePath })

                   val intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
                    intent.putStringArrayListExtra("VIDEO_PATHS", videoPaths)
                   intent.putExtra("position" , position)

                   startActivity(intent)

            }

        })
//        MainActivity.viewModel

        binding.rvVideoList.layoutManager = LinearLayoutManager(activity)
        binding.rvVideoList.adapter= adapter

        MainActivity.viewModel.videoLiveData.observe(requireActivity(), Observer { imageFiles ->

            if (imageFiles.isNotEmpty()){
                binding.tvNoData.visibility = View.GONE
            }


            adapter.setImages(imageFiles)
        })


        return view

    }

    override fun onResume() {
        super.onResume()
        if (MainActivity.viewModel!=null){
            MainActivity.viewModel.loadVideo()
        }
    }




}