package com.practical.photocutpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ImageRepository(application)
    val imagesLiveData: LiveData<List<File>> get() = repository.imagesLiveData
    val videoLiveData: LiveData<List<File>> get() = repository.videoLiveData
    var player: ExoPlayer? = null


    fun loadImages() {
        repository.fetchImages()
    }

    fun loadVideo(){
        repository.fetchVideos()
    }


    override fun onCleared() {
        player?.release()
        super.onCleared()
    }

}
