package com.example.a05_photogallery

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*

// viewModel에서 공유 preference를 사용하려면 application context가 필요하다.
// 고로, ViewModel이 아닌 AndroidViewModel을 가져와야 한다.
class PhotoGalleryViewModel(private val app: Application): AndroidViewModel(app) {
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val flickrFetchr = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
    get() = mutableSearchTerm.value ?: ""
    // 이전에 검색하면서 저장한 query 문자열을 다시 띄워줌

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
//        galleryItemLiveData = FlickrFetchr().searchPhotos("sky")
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if(searchTerm.isBlank()) { // 사용자가 쿼리 문자열을 지우면 값이 없는 쿼리 문자열로 검색하지 않고 최근 사진들로 가져오도록 변경
                flickrFetchr.fetchPhotos()
            } else {
                flickrFetchr.searchPhotos(searchTerm)
            }
        }
        //mutableSearchTerm을 람다 함수에 적용하여 liveData로 반환한다.
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
    }

    // 백 버튼처럼 아예 종료되면 데이터를 다운로드받는 동작을 취소한다.
    override fun onCleared() {
        super.onCleared()
        Log.d("houhou","다운로드 취소")
        flickrFetchr.cancelOnCleared()
    }
}