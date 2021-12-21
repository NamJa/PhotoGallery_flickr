package com.example.a05_photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.a05_photogallery.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr {
    private val flickrApi: FlickrApi

    init {
        // addConverterFactory(ScalarsConverterFactory.create())를 추가하면, FlickrApi 인터페이스에서 지정한 Call<String>으로 변환된다.
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    // 다른 컴포넌트가 변경하는 행위를 방지하기 위해 non-mutable 타입 사용
    fun fetchContents(): LiveData<String> {
        val responseLiveData: MutableLiveData<String> = MutableLiveData()
        val flickrRequest: Call<String> = flickrApi.fetchContents()

        // Call.enqueue는 요청을 백그라운드 스레드에서 실행한다.
        flickrRequest.enqueue(object : Callback<String> {
            // 백그라운드 스레드에서 실행되는 요청이 완료되면 Retrofit은 main(UI) 스레드에 제공된 콜백 함수 중 하나를 호출한다.
            // 그 함수들은 밑의 함수들이면 이 중에 한 개가 선택된다.
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG, "response received")
                responseLiveData.value = response.body()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "failed to fetch photos", t)
            }
        })

        return responseLiveData
    }
}