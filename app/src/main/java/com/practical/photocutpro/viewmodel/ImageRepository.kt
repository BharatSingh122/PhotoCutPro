package com.practical.photocutpro.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File

class ImageRepository(private val application: Application) {
    private val _imagesLiveData = MutableLiveData<List<File>>()
    val imagesLiveData: LiveData<List<File>> get() = _imagesLiveData

    private val _videoLiveData = MutableLiveData<List<File>>()
    val videoLiveData: LiveData<List<File>> get() = _videoLiveData


    fun fetchImages() {

        val path : String = "${application.cacheDir}/image"
        val imagesDir = File(path)

        if (imagesDir.exists() && imagesDir.isDirectory) {
            val imageFiles = imagesDir.listFiles { _, name ->
                name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")
            }?.toList() ?: emptyList()
            _imagesLiveData.postValue(imageFiles)
        } else {
            _imagesLiveData.postValue(emptyList())
        }
    }

    fun fetchVideos() {

        val path : String = "${application.cacheDir}/video"
        val imagesDir = File(path)

        if (imagesDir.exists() && imagesDir.isDirectory) {
            val imageFiles = imagesDir.listFiles { _, name ->
                name.endsWith(".mp4")
            }?.toList() ?: emptyList()
            _videoLiveData.postValue(imageFiles)
        } else {
            _videoLiveData.postValue(emptyList())
        }
    }
}
