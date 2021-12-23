package com.example.a05_photogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.a05_photogallery.api.FlickrApi
import com.example.a05_photogallery.api.FlickrResponse
import com.example.a05_photogallery.api.PhotoInterceptor
import com.example.a05_photogallery.api.PhotoResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr {
    private val flickrApi: FlickrApi
    private lateinit var flickrRequest: Call<FlickrResponse>

    init {
        // 인터셉터 생성
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        // addConverterFactory(ScalarsConverterFactory.create())를 추가하면, FlickrApi 인터페이스에서 지정한 Call<String>으로 변환된다.
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        // interceptor 등록을 위한 Retrofit.client() 호출

       flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.fetchPhotos())
    }

    fun searchPhotos(query: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.searchPhotos(query))
    }

    // 다른 컴포넌트가 변경하는 행위를 방지하기 위해 non-mutable 타입 사용
    private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>): LiveData<List<GalleryItem>> {
//        val responseLiveData: MutableLiveData<String> = MutableLiveData()
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

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

    // 백그라운드 스레드에서 호출되어야함을 나타내는 annotation
    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap=$bitmap from Response=$response")
        return bitmap
    }

    // 데이터 다운로드 취소를 위한 함수
    fun cancelOnCleared() {
        if(::flickrRequest.isInitialized)
            flickrRequest.cancel()
    }
}