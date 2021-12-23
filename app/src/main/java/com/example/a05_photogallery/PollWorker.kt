package com.example.a05_photogallery

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "PollWorker"

class PollWorker(val context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    // 백그라운드 함수에서 호출된다. 최대 10분까지 오래 실행되는 작업을 실행할 수 있다.
    override fun doWork(): Result {
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context)
        val items: List<GalleryItem> = if (query.isEmpty()) {
            FlickrFetchr().fetchPhotosRequest()
                .execute()
                .body()
                ?.photos
                ?.galleryItems
        } else {
            FlickrFetchr().searchPhotosRequest(query)
                .execute()
                .body()
                ?.photos
                ?.galleryItems
        } ?: emptyList()

        if(items.isEmpty()) {
            return Result.success()
        }
        val resultId = items.first().id
        if(resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result: $resultId")
            QueryPreferences.setLastResultId(context, lastResultId)
        }

        return Result.success()
        //작업이 성공적으로 완료되었음을 나타내는 Result 클래스 인스턴스를 반환한다.
        // Result.success() 말고도 실패하면 다시 실행되지 않는
        // Result.failure() 함수 및 에러가 생겨서 이후에 작업을 다시 실행하겠다는 Result.retry()도 있다.
    }
}