package com.example.a05_photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.a05_photogallery.api.FlickrApi
import com.example.a05_photogallery.api.FlickrResponse
import com.example.a05_photogallery.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr {
    private val flickrApi: FlickrApi

    init {
        // addConverterFactory(ScalarsConverterFactory.create())를 추가하면, FlickrApi 인터페이스에서 지정한 Call<String>으로 변환된다.
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    // 다른 컴포넌트가 변경하는 행위를 방지하기 위해 non-mutable 타입 사용
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
//        val responseLiveData: MutableLiveData<String> = MutableLiveData()
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val flickrRequest: Call<FlickrResponse> = flickrApi.fetchPhotos()

        // Call.enqueue는 요청을 백그라운드 스레드에서 실행한다.
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            // 백그라운드 스레드에서 실행되는 요청이 완료되면 Retrofit은 main(UI) 스레드에 제공된 콜백 함수 중 하나를 호출한다.
            // 그 함수들은 밑의 함수들이면 이 중에 한 개가 선택된다.
            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "response received")
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value = galleryItems
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "failed to fetch photos", t)
            }
        })

        return responseLiveData
    }
}