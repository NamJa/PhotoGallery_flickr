package com.example.a05_photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a05_photogallery.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val flickrHomePageRequest: Call<String> = flickrApi.fetchContents()
//        // 웹 요청을 실행하는것이 아닌 Call 객체를 반환한다.
//
//        // Call.enqueue는 요청을 백그라운드 스레드에서 실행한다.
//        flickrHomePageRequest.enqueue(object : Callback<String> {
//
//            // 백그라운드 스레드에서 실행되는 요청이 완료되면 Retrofit은 main(UI) 스레드에 제공된 콜백 함수 중 하나를 호출한다.
//            // 그 함수들은 밑의 함수들이면 이 중에 한 개가 선택된다.
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                Log.d(TAG, "response received: ${response.body()}")
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.e(TAG, "failed to fetch photos", t)
//            }
//        })
        val flickrLiveData: LiveData<String> = FlickrFetchr().fetchContents()
        flickrLiveData.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString")
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        return view
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}