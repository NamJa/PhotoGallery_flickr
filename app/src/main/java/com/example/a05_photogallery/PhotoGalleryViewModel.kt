package com.example.a05_photogallery

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel: ViewModel() {
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val flickrFetchr = FlickrFetchr()

    init {
        galleryItemLiveData = FlickrFetchr().fetchPhotos()
    }

    // 백 버튼처럼 아예 종료되면 데이터를 다운로드받는 동작을 취소한다.
    override fun onCleared() {
        super.onCleared()
        Log.d("houhou","다운로드 취소")
        flickrFetchr.cancelOnCleared()
    }
}