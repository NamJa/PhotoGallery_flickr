package com.example.a05_photogallery.api

import retrofit2.Call
import retrofit2.http.GET


interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList"
            +"&api_key=0cd992174ec6b168e0a133912d8fb0ca"
            +"&format=json"
            +"&nojsoncallback=1"
            +"&extras=url_s")
    fun fetchPhotos(): Call<FlickrResponse>
}