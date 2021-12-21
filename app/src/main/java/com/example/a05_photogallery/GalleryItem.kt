package com.example.a05_photogallery

import com.google.gson.annotations.SerializedName


data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s") var url: String = ""
)
// @SerializedName은 여기서 지정한 변수(url)와 가져올 데이터의 json 속성(url_s)가 일치하지 않을 때 사용하는 어노테이션이다.
// title과 id 변수처럼 가져올 json 데이터의 속성과 같다면 그대로 사용해도 된다.