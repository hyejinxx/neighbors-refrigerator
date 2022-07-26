package com.neighbor.neighborsrefrigerator.viewmodels

import android.icu.text.StringSearch
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.neighbor.neighborsrefrigerator.data.PostData
import com.neighbor.neighborsrefrigerator.network.DBAccessModule
import kotlinx.coroutines.flow.MutableStateFlow
import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostViewModel : ViewModel() {

    private val dbAccessModule = DBAccessModule()
    var sharePostsByTime = MutableStateFlow<ArrayList<PostData>?>(null)
    var sharePostsByDistance = MutableStateFlow<ArrayList<PostData>?>(null)
    var seekPostsByTime = MutableStateFlow<ArrayList<PostData>?>(null)
    var searchedPosts = MutableStateFlow<ArrayList<PostData>?>(null)

    var timeStamp = SimpleDateFormat("yyyy-MM-dd HH:MM:ss").format(Date(System.currentTimeMillis()))

    init {
        dbAccessModule.getPostOrderByTime(3, 1,0, 12, null, null, timeStamp) { sharePostsByTime.value = it }
        dbAccessModule.getPostOrderByTime(3, 2, 0, 12, null, null, timeStamp) { seekPostsByTime.value = it }
    }

    fun search(item: String?, category:Int?, reqType: String, postType: String, currentIndex: Int, num: Int, applyPostData : (ArrayList<PostData>) -> Unit){

        dbAccessModule.getPostOrderByTime(
            reqType = when(reqType){
                "category" -> 1
                "search" -> 2
                "justTime" -> 3
                else -> 1
            },
            postType = when(postType){
                "share" -> 1
                "seek" -> 2
                else -> 1
            },
            currentIndex = currentIndex,
            num = num,
            categoryId = category, // null일수 있음
            productName = item,
            currentTime = timeStamp)
        {
            applyPostData(it)
        }

    }

    fun changeTime(){
        timeStamp = SimpleDateFormat("yyyy-MM-dd HH:MM:ss").format(Date(System.currentTimeMillis()))
    }
}