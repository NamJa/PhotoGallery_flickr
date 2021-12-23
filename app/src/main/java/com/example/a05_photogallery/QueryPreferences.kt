package com.example.a05_photogallery

import android.content.Context
import android.preference.PreferenceManager

private const val PREF_SEARCH_QUERY = "searchQuery"

// 싱글톤 패턴을 위해 class가 아닌 object 키워드 사용
object QueryPreferences {
    fun getStoredQuery(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        //PREF_SEARCH_QUERY키가 없으면 null값이 반환된다. 하지만 여기서는 키 값이 null이 아니기 때문에 !!를 사용용
       return prefs.getString(PREF_SEARCH_QUERY, "")!!
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SEARCH_QUERY, query)
            .apply()
        // apply를 하게 되면 변경 데이터를 메모리에 구성한 후, 백그라운드 스레드에서 실제 파일에 쓰게 된다.
    }
}