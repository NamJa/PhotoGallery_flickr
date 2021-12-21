package com.example.a05_photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
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
//        val flickrLiveData: LiveData<List<GalleryItem>> = FlickrFetchr().fetchPhotos()
//        flickrLiveData.observe(
//            this,
//            Observer { galleryItems ->
//                Log.d(TAG, "Response received: $galleryItems")
//            })

        // ViewModelProvider를 사용하게 되면 장치회전 같은 상황에서 fragment 인스턴스가 소멸하고 다시 생성되더라도
        // photoGalleryViewModel 인스턴스는 다시 생성되지 않고 메모리에 계속 보존되어 사용된다.
        photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 화면 회전이나 galleryItems의 구성 변경이 발생하더라도 대응 가능
        // 보통 jetpack ViewModel과 livedata는 장치 회전 겸 데이터 구성 변경에 대응하기 위한 조합으로 많이 사용된다.
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer { galleryItems ->
                Log.d(TAG, "Have gallery items from ViewModel $galleryItems")
            }
        )
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}