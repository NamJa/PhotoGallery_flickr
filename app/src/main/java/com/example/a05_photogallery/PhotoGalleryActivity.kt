package com.example.a05_photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        // null인지 검사하여 null이면 true를 반환하고 아니면 false를 반환한다. ==> 캬 신박하누
        val isFragmentContainerEmpty = savedInstanceState == null
        // savedInstanceState가 null이면 앱이 시작될 때 최초 실행된 액티비티 인스턴스임을 의미하므로 아직 fragment를 호스팅하지 않았음을 나타낸다.
        // 반면에 null이 아니면 장치 회전 등의 구성 변경이나 프로세스 종료로 액티비티 인스턴스가 다시 생성된 것이므로 이전에 호스팅된 fragment가 있음을 의미한다.

        if(isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}