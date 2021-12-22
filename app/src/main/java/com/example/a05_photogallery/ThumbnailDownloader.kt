package com.example.a05_photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap


private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG), LifecycleEventObserver {
    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrFetchr = FlickrFetchr()


    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if(msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T // 만약 T로 캐스팅할 수 없는 경우라면 @SuppressLint로 인해 android Lint 경고를 출력한다.
                    // 하지만, Suppress로 UNCHECKED_CAST와 관련된 오류는 출력하지 않는다.
                    // 어차피 이 플젝에서는 obj 속성이 갖는 참조와 T모두 PhotoHolder로 타입이 일치한다.
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    // Deprecated
//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun setup() {
//        Log.i(TAG, "Starting background thread")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun teardown() {
//        Log.i(TAG, "Destroying background thread")
//    }
    val fragmentLifecycleObserver : LifecycleEventObserver =
        object :LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if(event == Lifecycle.Event.ON_CREATE)
                    setup()
                else if (event == Lifecycle.Event.ON_DESTROY)
                    teardown()
            }
        }

    val viewLifecycleObserver : LifecycleEventObserver =
        object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if(event == Lifecycle.Event.ON_DESTROY) {
                    clearQueue()
                }
            }
        }

    fun setup() {
        Log.i(TAG, "Starting background thread")
        start()
        looper
    }
    fun teardown() {
        Log.i(TAG, "Destroying background thread")
        quit()
    }
    fun clearQueue() {
        Log.i(TAG, "Clearing all requests from queue")
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestMap.clear()
    }

    // fragment의 생명주기를 인식하여 동작할 수 있게끔 한다.
    // fragment의 onCreate()나 onDestroy() 함수에 직접 작성할 수도 있지만 핸들러(다운로더) 자체에서 관리하는 편이 훨씬 좋다
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == Lifecycle.Event.ON_CREATE)
            setup()
        else if (event == Lifecycle.Event.ON_DESTROY)
            teardown()
    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a Url: $url")
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return

        responseHandler.post(Runnable {
            if(requestMap[target] != url || hasQuit) {
                return@Runnable
            }

            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })
    }
}