package com.example.a05_photogallery.api

import com.example.a05_photogallery.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
    // json의 photo 속성 안에 있는 사진 데이터들을 list로 구성하여 받기 위함
}