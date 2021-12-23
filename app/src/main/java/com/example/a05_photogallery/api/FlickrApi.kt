package com.example.a05_photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


interface FlickrApi {
//    @GET("services/rest/?method=flickr.interestingness.getList"
//            +"&api_key=0cd992174ec6b168e0a133912d8fb0ca"
//            +"&format=json"
//            +"&nojsoncallback=1"
//            +"&extras=url_s")
// Interceptor로 인해 필요 없어졌다.
    @GET("services/rest?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>

    @GET("services/rest?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse>
    // searchPhotos("robot")을 호출하면 text=robot이 url 끝에 추가된다.
}